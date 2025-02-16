package digital.fischers.locationshare.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import digital.fischers.locationshare.data.database.entities.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert
    suspend fun insertLocation(location: LocationEntity)

    @Query("SELECT * FROM LocationEntity")
    suspend fun getAllLocations(): List<LocationEntity>

    @Query("SELECT * FROM LocationEntity WHERE timestamp > :timestamp AND userId = :userId")
    suspend fun getAllLocationsYoungerThan(timestamp: Long, userId: String): List<LocationEntity>

    @Query("SELECT * FROM LocationEntity WHERE userId = :friendId ORDER BY timestamp DESC LIMIT 1")
    fun getLocationByFriendIdStream(friendId: String): Flow<LocationEntity?>

    @Delete
    suspend fun deleteLocation(location: LocationEntity)

    @Upsert
    suspend fun upsertLocation(location: LocationEntity)
}