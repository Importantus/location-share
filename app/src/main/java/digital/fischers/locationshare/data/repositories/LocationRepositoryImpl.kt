package digital.fischers.locationshare.data.repositories

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.annotation.MainThread
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import digital.fischers.locationshare.broadcastReceivers.LocationReceiver
import digital.fischers.locationshare.data.database.daos.LocationDao
import digital.fischers.locationshare.data.database.entities.toEntity
import digital.fischers.locationshare.data.keyValueStorage.Storage
import digital.fischers.locationshare.data.remote.APIError
import digital.fischers.locationshare.data.remote.APIResult
import digital.fischers.locationshare.data.remote.ApiPath
import digital.fischers.locationshare.data.remote.LocationApi
import digital.fischers.locationshare.data.remote.apiCall
import digital.fischers.locationshare.data.remote.appendToServerUrl
import digital.fischers.locationshare.data.remote.getAccessTokenAndServerUrl
import digital.fischers.locationshare.domain.repositories.LocationRepository
import digital.fischers.locationshare.workers.SyncWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationRepositoryImpl @Inject constructor(
    private val api: LocationApi,
    val context: Context,
    val locationDao: LocationDao,
) : LocationRepository {

    private val _receivingLocationUpdates = MutableStateFlow(false)

    /**
     * Status of location updates, i.e., whether the app is actively subscribed to location changes.
     */
    val receivingLocationUpdates: StateFlow<Boolean>
        get() = _receivingLocationUpdates

    // The Fused Location Provider provides access to location APIs.
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val provider = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> LocationManager.FUSED_PROVIDER
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
        else -> LocationManager.NETWORK_PROVIDER
    }

    // Stores parameters for requests to the FusedLocationProviderApi.
    private val locationRequest: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 60000).build()

    /**
     * Creates default PendingIntent for location changes.
     *
     * Note: We use a BroadcastReceiver because on API level 26 and above (Oreo+), Android places
     * limits on Services.
     */
    private val locationUpdatePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, LocationReceiver::class.java)
        intent.action = LocationReceiver.ACTION_PROCESS_UPDATES
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
    }


    /**
     * Uses the FusedLocationProvider to start location updates if the correct fine locations are
     * approved.
     *
     * @throws SecurityException if ACCESS_FINE_LOCATION permission is removed before the
     * FusedLocationClient's requestLocationUpdates() has been completed.
     */
    @Throws(SecurityException::class)
    @MainThread
    override fun startLocationUpdates() {

//        if (!context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) return

        try {
            Log.d("LocationRepositoryImpl", "startLocationUpdates()")
            _receivingLocationUpdates.value = true
            // If the PendingIntent is the same as the last request (which it always is), this
            // request will replace any requestLocationUpdates() called before.
            locationManager.requestLocationUpdates(provider, 60000, 0f, locationUpdatePendingIntent)
//            fusedLocationClient.requestLocationUpdates(locationRequest, locationUpdatePendingIntent)
        } catch (permissionRevoked: SecurityException) {
            _receivingLocationUpdates.value = false

            // Exception only occurs if the user revokes the FINE location permission before
            // requestLocationUpdates() is finished executing (very rare).
//            Log.d(TAG, "Location permission revoked; details: $permissionRevoked")
            throw permissionRevoked
        }
    }

    @MainThread
    override fun stopLocationUpdates() {
        Log.d("LocationRepositoryImpl", "stopLocationUpdates()")
//        fusedLocationClient.removeLocationUpdates(locationUpdatePendingIntent)
        locationManager.removeUpdates(locationUpdatePendingIntent)
        _receivingLocationUpdates.value = false
    }

    override fun areLocationUpdatesEnabled(): Flow<Boolean> {
        return receivingLocationUpdates
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocationSuspend(): Location = suspendCancellableCoroutine { continuation ->
        locationManager.getCurrentLocation(provider, null, context.mainExecutor) { location ->
            if (location != null) {
                continuation.resume(location)
            } else {
                continuation.resumeWithException(Exception("Failed to get location"))
            }
        }
    }

    override suspend fun insertLocation(location: Location) {
        val user = Storage(context).getUser()
        if (user == null) {
            Log.d("LocationRepositoryImpl", "User is null")
            return
        }
        val locationEntity = location.toEntity(user.id, user.sessionId, context)
        locationDao.insertLocation(locationEntity)
    }

    /**
     * Checks if the database has a location that is younger than the last sync time. If not, it
     * gets the current location and inserts it into the database.
     */
    override suspend fun ensureDbHasLocation() {
        val locations = locationDao.getAllLocationsYoungerThan(Storage(context).getLastSyncTime())
        if (locations.isEmpty()) {
            val location = getCurrentLocationSuspend()
            val user = Storage(context).getUser()
            if (user == null) {
                Log.d("LocationRepositoryImpl", "User is null")
                return
            }
            val locationEntity = location.toEntity(user.id, user.sessionId, context)
            locationDao.insertLocation(locationEntity)
        }
    }

    override fun startDataSync() {
        Log.d("LocationRepositoryImpl", "startDataSync")
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true).build()

        val sync =
            PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES).setConstraints(constraints)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            sync
        )
    }

    override fun stopDataSync() {
        Log.d("LocationRepositoryImpl", "stopDataSync")
        WorkManager.getInstance(context).cancelUniqueWork(SyncWorker.WORK_NAME)
        WorkManager.getInstance(context).cancelAllWorkByTag(SyncWorker.WORK_NAME)
    }

    override fun isDataSyncEnabled(): Flow<Boolean> {
        return WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow(SyncWorker.WORK_NAME)
            .map { workInfos ->
                workInfos.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
            }
    }

    override suspend fun sendLocationData(): APIResult<Unit> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(context)
        if (accessToken == null) {
            return APIResult.Error(
                APIError.CustomError(
                    "No access token found",
                    401,
                    "The user has to be logged in to create send location data"
                )
            )
        }
        val locations = locationDao.getAllLocationsYoungerThan(Storage(context).getLastSyncTime())
        val result = apiCall {
            api.createLocation(
                url = appendToServerUrl(serverUrl, ApiPath.LOCATIONS),
                token = accessToken,
                body = locations
            )
        }
        return when (result) {
            is APIResult.Success -> {
                Storage(context).setLastSyncTime(Date().time)
                result
            }

            else -> result
        }
    }
}