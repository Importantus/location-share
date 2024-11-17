package digital.fischers.locationshare.data.repositories

import android.content.Context
import android.util.Log
import digital.fischers.locationshare.data.database.daos.LocationDao
import digital.fischers.locationshare.data.keyValueStorage.Storage
import digital.fischers.locationshare.data.remote.LocationApi
import digital.fischers.locationshare.data.remote.LocationData
import digital.fischers.locationshare.data.remote.apiCall
import digital.fischers.locationshare.domain.repositories.RemoteRepository
import digital.fischers.locationshare.utils.getDeviceName
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(
    private val api: LocationApi,
    private val locationDao: LocationDao,
    val context: Context
) : RemoteRepository {
    override suspend fun sendLocationData() {
        Log.d("RemoteRepositoryImpl", "sendLocationData")
        val locations = locationDao.getAllLocations()
        val lastSync = Storage(context).getLastSyncTime()
        val currentAppVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        locations.filter {
            it.timestamp > lastSync
        }.forEach {
            val locationData = LocationData(
                name = getDeviceName(context.contentResolver),
                location = "${it.latitude},${it.longitude}",
                velocity = it.speed.toString(),
                more = "${it.timestamp.toString()}, $currentAppVersion, ${it.more}"
            )
            Log.d("RemoteRepositoryImpl", "sendLocationData: $locationData")
            apiCall { api.sendLocation(locationData) }
        }
        Storage(context).setLastSyncTime(System.currentTimeMillis())
    }
}