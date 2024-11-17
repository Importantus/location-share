package digital.fischers.locationshare.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import digital.fischers.locationshare.data.database.entities.LocationEntity

@Dao
interface LocationDao {
    @Insert
    suspend fun insertLocation(location: LocationEntity)

    @Query("SELECT * FROM LocationEntity")
    suspend fun getAllLocations(): List<LocationEntity>
}