package digital.fischers.locationshare.domain.repositories

import digital.fischers.locationshare.data.remote.APIResult
import digital.fischers.locationshare.data.remote.types.CreateShareRequest
import digital.fischers.locationshare.data.remote.types.CreateUserResponse
import digital.fischers.locationshare.domain.types.Friend
import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    fun getFriendsStream(): Flow<List<Friend>>
    fun getFriendStream(friendId: String): Flow<Friend?>

    suspend fun addShare(data: CreateShareRequest): APIResult<Unit>
    suspend fun removeShare(shareId: String): APIResult<Unit>

    suspend fun sendWakeUp(friendId: String): APIResult<Unit>

    suspend fun syncFriends(): APIResult<Unit>

    suspend fun syncFriendsLocations(): APIResult<Unit>

    suspend fun listenForUpdates()

    suspend fun getAllUsersOfServer(): APIResult<List<CreateUserResponse>>
}