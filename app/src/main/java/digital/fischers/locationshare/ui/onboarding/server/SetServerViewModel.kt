package digital.fischers.locationshare.ui.onboarding.server

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.locationshare.data.remote.APIResult
import digital.fischers.locationshare.domain.repositories.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetServerViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var serverUrl by mutableStateOf("")
        private set

    var validServerUrl by mutableStateOf(false)
        private set

    var loading by mutableStateOf(false)
        private set

    private var serverUrlCheckJob: Job? = null

    init {
        viewModelScope.launch {
            serverUrl = userRepository.getServerUrl() ?: ""
            validServerUrl = checkServerUrl()
        }
    }

    fun onServerUrlChanged(url: String) {
        serverUrl = url
        serverUrlCheckJob?.cancel() // Cancel previous job
        serverUrlCheckJob = viewModelScope.launch {
            delay(1000) // Delay for 50ms
            validServerUrl = checkServerUrl()
        }
    }

    suspend fun saveServerUrl(url: String) {
        userRepository.setServerUrl(url)
    }

    private suspend fun checkServerUrl(): Boolean {
        loading = true
        if (serverUrl.isBlank() || !serverUrl.startsWith("http")) {
            loading = false
            return false
        } else {
            val result = userRepository.getServerInfo(serverUrl)
            loading = false
            return result is APIResult.Success
        }
    }
}