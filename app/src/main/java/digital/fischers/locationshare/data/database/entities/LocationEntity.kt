package digital.fischers.locationshare.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val speed: Float,
    val timestamp: Long,
    @ColumnInfo(defaultValue = "")
    val more: String
)

