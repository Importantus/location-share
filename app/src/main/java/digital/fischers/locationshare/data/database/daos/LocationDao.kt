package digital.fischers.locationshare.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import digital.fischers.locationshare.data.database.entities.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert
    suspend fun insertLocation(location: LocationEntity)

    @Query("SELECT * FROM LocationEntity")
    suspend fun getAllLocations(): List<LocationEntity>

    @Query("SELECT * FROM LocationEntity WHERE timestamp > :timestamp")
    suspend fun getAllLocationsYoungerThan(timestamp: Long): List<LocationEntity>

    @Query("SELECT * FROM LocationEntity WHERE userId = :userId")
    fun getLocationByFriendIdStream(friendId: String): Flow<LocationEntity?>

    @Delete
    suspend fun deleteLocation(location: LocationEntity)
}