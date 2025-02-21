package digital.fischers.locationshare.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import digital.fischers.locationshare.domain.repositories.LocationRepository
import digital.fischers.locationshare.domain.repositories.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var locationRepository: LocationRepository

    @Inject
    lateinit var userRepository: UserRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MyFirebaseMS", "New FCM token: $token")

        CoroutineScope(Dispatchers.IO).launch {
            userRepository.registerFcmToken()
        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.data["action"]?.let { action ->
            if (action == "REQUEST_LOCATION_UPDATE") {
                Log.d("MyFirebaseMS", "Received location update request")
                requestLocationUpdate()
            }
        }
    }

    private fun requestLocationUpdate() {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                locationRepository.fillDBWithCurrentLocation()
                locationRepository.sendLocationData()
            }
        } catch (e: SecurityException) {
            Log.e("MyFirebaseMS", "Missing location permission", e)
        }
    }
}
