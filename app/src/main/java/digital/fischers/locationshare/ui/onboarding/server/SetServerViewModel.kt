package digital.fischers.locationshare.ui.onboarding.server

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.locationshare.domain.repositories.UserRepository
import javax.inject.Inject

@HiltViewModel
class SetServerViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var serverUrl by mutableStateOf("")
        private set

    val validServerUrl by derivedStateOf {
        serverUrl.isNotBlank() && serverUrl.startsWith("http")
    }

    fun onServerUrlChanged(url: String) {
        serverUrl = url
    }
}