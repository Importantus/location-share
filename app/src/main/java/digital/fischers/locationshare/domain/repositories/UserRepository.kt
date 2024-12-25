package digital.fischers.locationshare.domain.repositories

import digital.fischers.locationshare.data.keyValueStorage.entities.UserEntity
import digital.fischers.locationshare.data.remote.APIResult
import digital.fischers.locationshare.data.remote.types.CreateUserRequest
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun isLoggedInStream(): Flow<Boolean>
    fun getUserStream(): Flow<UserEntity?>

    suspend fun login(username: String, password: String): APIResult<UserEntity>
    suspend fun logout()
    suspend fun register(createUserRequest: CreateUserRequest): APIResult<UserEntity>

    suspend fun setServerUrl(serverUrl: String)
    suspend fun getServerUrl(): String
    suspend fun getServerURLStream(): Flow<String>

    suspend fun updateUser(user: UserEntity): APIResult<UserEntity>

    suspend fun hasSeenOnboarding(): Boolean
    suspend fun setOnboardingSeen(value: Boolean)
}