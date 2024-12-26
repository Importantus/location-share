package digital.fischers.locationshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import digital.fischers.locationshare.domain.repositories.LocationRepository
import digital.fischers.locationshare.ui.LocationShareApp
import digital.fischers.locationshare.ui.theme.LocationShareTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var locationRepository: LocationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocationShareTheme {
                LocationShareApp(
                    intent = intent
                )
            }
        }
    }
}