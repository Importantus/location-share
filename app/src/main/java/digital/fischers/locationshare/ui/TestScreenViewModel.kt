package digital.fischers.locationshare.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.locationshare.MainActivity
import digital.fischers.locationshare.domain.repositories.LocationRepository
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TestScreenViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    val isLocationSharingEnabled = locationRepository.areLocationUpdatesEnabled().stateIn(
        scope = viewModelScope, started = WhileSubscribed(5_000L), initialValue = false
    )

    val isDataSyncEnabled = locationRepository.isDataSyncEnabled().stateIn(
        scope = viewModelScope, started = WhileSubscribed(5_000L), initialValue = false
    )

    private fun enableLocationSharing() {
        locationRepository.startLocationUpdates()
        locationRepository.startDataSync()
    }

    private fun disableLocationSharing() {
        locationRepository.stopLocationUpdates()
        locationRepository.stopDataSync()
    }

    fun toggleLocationSharing() {
        if (isDataSyncEnabled.value) {
            disableLocationSharing()
        } else {
            enableLocationSharing()
        }
    }
}