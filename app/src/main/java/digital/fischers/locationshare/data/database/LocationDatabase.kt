package digital.fischers.locationshare.data.database

import android.content.Context
import androidx.room.Database
import digital.fischers.locationshare.data.database.daos.FriendDao
import digital.fischers.locationshare.data.database.daos.LocationDao
import digital.fischers.locationshare.data.database.entities.FriendEntity
import digital.fischers.locationshare.data.database.entities.LocationEntity

@Database(
    entities = [
        LocationEntity::class,
        FriendEntity::class
    ],
    version = 1,
    autoMigrations = [
    ],
    exportSchema = true
)
abstract class LocationDatabase : androidx.room.RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun friendDao(): FriendDao

    companion object {
        @Volatile
        private var INSTANCE: LocationDatabase? = null

        fun getDatabase(context: Context): LocationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    LocationDatabase::class.java,
                    "location_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}