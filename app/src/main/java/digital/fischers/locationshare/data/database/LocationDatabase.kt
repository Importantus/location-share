package digital.fischers.locationshare.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import digital.fischers.locationshare.data.database.daos.FriendDao
import digital.fischers.locationshare.data.database.daos.LocationDao
import digital.fischers.locationshare.data.database.entities.LocationEntity

@Database(
    entities = [LocationEntity::class], version = 3, autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 1, to = 3),
        AutoMigration(from = 2, to = 3)
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