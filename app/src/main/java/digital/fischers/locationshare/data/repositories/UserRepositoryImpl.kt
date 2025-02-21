package digital.fischers.locationshare.data.repositories

import android.content.Context
import androidx.work.await
import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import digital.fischers.locationshare.data.keyValueStorage.Storage
import digital.fischers.locationshare.data.keyValueStorage.entities.UserEntity
import digital.fischers.locationshare.data.remote.APIError
import digital.fischers.locationshare.data.remote.APIResult
import digital.fischers.locationshare.data.remote.ApiPath
import digital.fischers.locationshare.data.remote.LocationApi
import digital.fischers.locationshare.data.remote.apiCall
import digital.fischers.locationshare.data.remote.appendToServerUrl
import digital.fischers.locationshare.data.remote.getAccessTokenAndServerUrl
import digital.fischers.locationshare.data.remote.types.CreateSessionRequest
import digital.fischers.locationshare.data.remote.types.CreateUserRequest
import digital.fischers.locationshare.data.remote.types.Info
import digital.fischers.locationshare.data.remote.types.RegisterFCMTokenRequest
import digital.fischers.locationshare.data.remote.types.UpdateUserRequest
import digital.fischers.locationshare.domain.repositories.UserRepository
import digital.fischers.locationshare.utils.getDeviceName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: LocationApi,
    val context: Context,
) : UserRepository {
    override fun isLoggedInStream(): Flow<Boolean> {
        return Storage(context).getUserStream()
            .map { it != null && it.sessionId.isNotEmpty() && it.id.isNotEmpty() && it.authToken.isNotEmpty() }
    }

    override suspend fun isLoggedIn(): Boolean {
        val user = Storage(context).getUser()
        return user != null && user.sessionId.isNotEmpty() && user.id.isNotEmpty() && user.authToken.isNotEmpty()
    }

    override fun getUserStream(): Flow<UserEntity?> {
        return Storage(context).getUserStream().map { it }
    }

    override suspend fun getUser(): UserEntity? {
        return Storage(context).getUser()
    }

    override suspend fun login(username: String, password: String): APIResult<UserEntity> {
        val serverUrl = Storage(context).getServerUrl()
        val loginResponse = apiCall {
            api.createSession(
                url = appendToServerUrl(serverUrl, ApiPath.SESSIONS),
                body = CreateSessionRequest(
                    name = getDeviceName(context.contentResolver),
                    writing = true,
                    username = username,
                    password = password
                )
            )
        }

        return when (loginResponse) {
            is APIResult.Success -> {
                val user = UserEntity(
                    name = loginResponse.data.user.name,
                    username = loginResponse.data.user.username,
                    id = loginResponse.data.user.id,
                    authToken = loginResponse.data.token,
                    sessionId = loginResponse.data.id,
                    email = loginResponse.data.user.email
                )
                Storage(context).setUser(
                    user
                )
                registerFcmToken()
                APIResult.Success(user)
            }

            is APIResult.Error -> {
                APIResult.Error(exception = loginResponse.exception)
            }
        }
    }

    override suspend fun logout() {
        Storage(context).setUser(
            UserEntity(
                name = "",
                username = "",
                id = "",
                authToken = "",
                sessionId = "",
                email = ""
            )
        )
    }

    override suspend fun register(createUserRequest: CreateUserRequest): APIResult<UserEntity> {
        val serverUrl = Storage(context).getServerUrl()
        val registerResponse = apiCall {
            api.createUser(
                url = appendToServerUrl(serverUrl, ApiPath.USERS),
                body = createUserRequest
            )
        }

        return when (registerResponse) {
            is APIResult.Success -> {
                login(createUserRequest.username, createUserRequest.password)
            }

            is APIResult.Error -> {
                APIResult.Error(exception = registerResponse.exception)
            }
        }
    }

    override suspend fun registerFcmToken(): APIResult<Unit> {
        val token: String? = withContext(Dispatchers.IO) {
            try {
                FirebaseMessaging.getInstance().token.await()
            } catch (e: Exception) {
                // Handle the exception, e.g., log it
                println("Error getting FCM token: ${e.message}")
                null
            }
        }

        if(token == null) {
            return APIResult.Error(
                APIError.CustomError(
                    "No FCM token found",
                    401,
                    "The user has to be logged in to register an fcm token"
                )
            )
        }

        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(context)

        if (accessToken == null) {
            return APIResult.Error(
                APIError.CustomError(
                    "No access token found",
                    401,
                    "The user has to be logged in to register an fcm token"
                )
            )
        }

        return apiCall {
            api.registerFCMToken(
                url = appendToServerUrl(serverUrl, ApiPath.REGISTER_FCM_TOKEN),
                token = accessToken,
                body = RegisterFCMTokenRequest(
                    token = token
                )
            )
        }
    }

    override suspend fun getServerUrl(): String {
        return Storage(context).getServerUrl()
    }

    override suspend fun getServerURLStream(): Flow<String> {
        return Storage(context).getServerURLStream()
    }

    override suspend fun setServerUrl(serverUrl: String) {
        Storage(context).setServerUrl(serverUrl)
    }

    override suspend fun updateUser(user: UserEntity): APIResult<UserEntity> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(context)
        if (accessToken == null) {
            return APIResult.Error(
                APIError.CustomError(
                    "No access token found",
                    401,
                    "The user has to be logged in to update the userdata"
                )
            )
        }

        val updateResponse = apiCall {
            api.updateUser(
                url = appendToServerUrl(serverUrl, ApiPath.USERS),
                token = accessToken,
                body = UpdateUserRequest(
                    name = user.name,
                    username = user.username,
                    email = user.email,
                )
            )
        }

        return when (updateResponse) {
            is APIResult.Success -> {
                val updatedUser = UserEntity(
                    name = updateResponse.data.name,
                    username = updateResponse.data.username,
                    id = user.id,
                    authToken = user.authToken,
                    sessionId = user.sessionId,
                    email = updateResponse.data.email
                )
                Storage(context).setUser(
                    updatedUser
                )
                APIResult.Success(updatedUser)
            }

            is APIResult.Error -> {
                APIResult.Error(exception = updateResponse.exception)
            }
        }
    }

    override suspend fun hasSeenOnboarding(): Boolean {
        return Storage(context).getOnboardingStep() >= 5
    }

    override fun hasSeenOnboardingStream(): Flow<Boolean> {
        return Storage(context).getOnboardingStepStream().map {
            it >= 5
        }
    }

    override suspend fun getOnboardingStep(): Int {
        return Storage(context).getOnboardingStep()
    }

    override fun getOnboardingStepStream(): Flow<Int> {
        return Storage(context).getOnboardingStepStream()
    }

    override suspend fun setOnboardingStep(step: Int) {
        return Storage(context).setOnboardingStep(step)
    }

    override suspend fun getServerInfo(serverUrl: String?): APIResult<Info> {
        return apiCall {
            api.getInfo(
                url = appendToServerUrl(serverUrl ?: Storage(context).getServerUrl(), ApiPath.INFO)
            )
        }
    }
}