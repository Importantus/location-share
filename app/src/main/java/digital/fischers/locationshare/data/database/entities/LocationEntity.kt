package digital.fischers.locationshare.data.database.entities

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.BatteryManager
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class LocationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "session_id")
    val sessionId: String,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    @ColumnInfo(name = "accuracy")
    val accuracy: Float,

    @ColumnInfo(name = "battery")
    val battery: Float?,

    @ColumnInfo(name = "altitude")
    val altitude: Double,

    @ColumnInfo(name = "bearing")
    val bearing: Float,

    @ColumnInfo(name = "bearing_accuracy")
    val bearingAccuracy: Float,

    @ColumnInfo(name = "speed")
    val speed: Float,

    @ColumnInfo(name = "provider")
    val provider: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long
)

fun Location.toEntity(userId: String, sessionId: String, context: Context): LocationEntity {
    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
        context.registerReceiver(null, ifilter)
    }

    val batteryPct: Float? = batteryStatus?.let { intent ->
        val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        level * 100 / scale.toFloat()
    }

    return LocationEntity(
        id = UUID.randomUUID().toString(),
        userId = userId,
        sessionId = sessionId,
        latitude = latitude,
        longitude = longitude,
        accuracy = accuracy,
        battery = batteryPct,
        altitude = altitude,
        bearing = bearing,
        bearingAccuracy = bearingAccuracyDegrees,
        speed = speed,
        provider = provider ?: "",
        timestamp = time
    )
}

