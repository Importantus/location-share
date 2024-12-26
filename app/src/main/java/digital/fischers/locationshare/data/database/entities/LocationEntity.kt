package digital.fischers.locationshare.data.database.entities

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.BatteryManager
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class LocationEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val sessionId: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val battery: Float?,
    val altitude: Double,
    val bearing: Float,
    val bearingAccuracy: Float,
    val speed: Float,
    val provider: String,
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

