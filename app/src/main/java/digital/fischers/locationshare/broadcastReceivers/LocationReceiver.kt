package digital.fischers.locationshare.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.AndroidEntryPoint
import digital.fischers.locationshare.data.database.LocationDatabase
import digital.fischers.locationshare.data.database.entities.LocationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var locationDatabase: LocationDatabase

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("LocationReceiver", "onReceive")
        val location = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED, Location::class.java)
            else -> intent.getParcelableExtra<Location>(LocationManager.KEY_LOCATION_CHANGED)
        }
        Log.d("LocationReceiver", "Location: $location")
        if (location != null) {
            val locationEntity = LocationEntity(
                latitude = location.latitude,
                longitude = location.longitude,
                speed = location.speed,
                timestamp = location.time,
                more = "accuracy: ${location.accuracy}, altitude: ${location.altitude}, bearing: ${location.bearing}, provider: ${location.provider}"
            )
            CoroutineScope(Dispatchers.IO).launch {
                locationDatabase.locationDao().insertLocation(locationEntity)
            }
        }
    }

    companion object {
        const val ACTION_PROCESS_UPDATES = "digital.fischers.locationshare.action.PROCESS_UPDATES"
    }
}