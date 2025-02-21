package digital.fischers.locationshare.domain.repositories

import android.location.Location
import digital.fischers.locationshare.data.remote.APIResult
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun startLocationUpdates()
    fun stopLocationUpdates()
    fun areLocationUpdatesEnabled(): Flow<Boolean>
    suspend fun getCurrentLocationSuspend(): Location
    fun getCurrentLocation(): Flow<Location>
    suspend fun ensureDbHasLocation()
    suspend fun fillDBWithCurrentLocation()
    suspend fun insertLocation(location: Location)

    suspend fun sendLocationData(): APIResult<Unit>

    fun startDataSync()
    fun stopDataSync()
    fun isDataSyncEnabled(): Flow<Boolean>
}