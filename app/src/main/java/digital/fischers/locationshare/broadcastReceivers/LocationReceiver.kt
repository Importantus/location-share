package digital.fischers.locationshare.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import digital.fischers.locationshare.domain.repositories.LocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var locationRepository: LocationRepository

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("LocationReceiver", "onReceive")
        val location = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED, Location::class.java)
            else -> intent.getParcelableExtra<Location>(LocationManager.KEY_LOCATION_CHANGED)
        }

        CoroutineScope(Dispatchers.IO).launch {
            if (location != null) {
                locationRepository.insertLocation(location)
            }
        }
    }

    companion object {
        const val ACTION_PROCESS_UPDATES = "digital.fischers.locationshare.action.PROCESS_UPDATES"
    }
}