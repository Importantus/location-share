package digital.fischers.locationshare.domain.repositories

import android.location.Location
import digital.fischers.locationshare.MainActivity
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun startLocationUpdates()
    fun stopLocationUpdates()
    fun areLocationUpdatesEnabled(): Flow<Boolean>
    suspend fun getCurrentLocationSuspend(): Location
    suspend fun ensureDbHasLocation()

    fun startDataSync()
    fun stopDataSync()
    fun isDataSyncEnabled(): Flow<Boolean>
}