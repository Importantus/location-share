package digital.fischers.locationshare.ui.onboarding.registration

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.locationshare.data.keyValueStorage.entities.UserEntity
import digital.fischers.locationshare.data.remote.APIResult
import digital.fischers.locationshare.data.remote.types.CreateUserRequest
import digital.fischers.locationshare.data.remote.types.Info
import digital.fischers.locationshare.domain.repositories.UserRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var serverInfo: Info? by mutableStateOf(null)
        private set

    var username by mutableStateOf("")
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var registrationSecret by mutableStateOf("")

    val validInputs by derivedStateOf {
        username.isNotEmpty() && name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
    }

    var loading by mutableStateOf(false)
    var error by mutableStateOf<APIResult<UserEntity>?>(null)

    init {
        viewModelScope.launch {
            val result = userRepository.getServerInfo()
            if (result is APIResult.Success) {
                serverInfo = result.data
            }
        }
    }

    fun onUsernameChanged(value: String) {
        username = value
    }

    fun onNameChanged(value: String) {
        name = value
    }

    fun onEmailChanged(value: String) {
        email = value
    }

    fun onPasswordChanged(value: String) {
        password = value
    }

    fun onRegistrationSecretChanged(value: String) {
        registrationSecret = value
    }

    suspend fun register(): APIResult<UserEntity> {
        loading = true
        error = null
        val result = userRepository.register(
            createUserRequest = CreateUserRequest(
                name, email, username, password, registrationSecret
            )
        )
        loading = false
        if (result is APIResult.Error) {
            error = result
        }
        return result
    }
}