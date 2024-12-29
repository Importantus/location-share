package digital.fischers.locationshare.ui.onboarding.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.locationshare.data.keyValueStorage.entities.UserEntity
import digital.fischers.locationshare.data.remote.APIError
import digital.fischers.locationshare.data.remote.APIResult
import digital.fischers.locationshare.domain.repositories.UserRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var username by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    var loading by mutableStateOf(false)
        private set

    var error: APIError? by mutableStateOf(null)
        private set

    fun onUsernameChanged(newUsername: String) {
        username = newUsername
    }

    fun onPasswordChanged(newPassword: String) {
        password = newPassword
    }

    suspend fun login(username: String, password: String): APIResult<UserEntity> {
        loading = true
        error = null
        val result = userRepository.login(username, password)
        when (result) {
            is APIResult.Success -> {
                // Navigate to the next screen
            }

            is APIResult.Error -> {
                error = result.exception
            }
        }

        loading = false
        return result
    }
}