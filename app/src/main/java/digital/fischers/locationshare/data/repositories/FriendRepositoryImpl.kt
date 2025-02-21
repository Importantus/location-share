package digital.fischers.locationshare.data.repositories

import android.content.Context
import android.util.Log
import digital.fischers.locationshare.data.database.daos.FriendDao
import digital.fischers.locationshare.data.database.daos.LocationDao
import digital.fischers.locationshare.data.database.entities.FriendEntity
import digital.fischers.locationshare.data.keyValueStorage.Storage
import digital.fischers.locationshare.data.remote.APIError
import digital.fischers.locationshare.data.remote.APIResult
import digital.fischers.locationshare.data.remote.ApiPath
import digital.fischers.locationshare.data.remote.LocationApi
import digital.fischers.locationshare.data.remote.apiCall
import digital.fischers.locationshare.data.remote.appendToServerUrl
import digital.fischers.locationshare.data.remote.getAccessTokenAndServerUrl
import digital.fischers.locationshare.data.remote.types.CreateShareRequest
import digital.fischers.locationshare.data.remote.types.CreateUserResponse
import digital.fischers.locationshare.data.remote.types.WakeUpRequest
import digital.fischers.locationshare.domain.repositories.FriendRepository
import digital.fischers.locationshare.domain.types.Friend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val friendDao: FriendDao,
    private val locationDao: LocationDao,
    private val api: LocationApi,
    val context: Context
) : FriendRepository {
    override fun getFriendsStream(): Flow<List<Friend>> {
        val friendsStream = friendDao.getFriendsStream()
        return friendsStream.map {
            it.map { friendEntity ->
                val locationEntity = locationDao.getLocationByFriendIdStream(friendEntity.id)
                Friend(
                    id = friendEntity.id,
                    name = friendEntity.name,
                    username = friendEntity.username,
                    theirShareId = friendEntity.theirShareId,
                    userShareId = friendEntity.userShareId,
                    location = locationEntity
                )
            }
        }
    }

    override fun getFriendStream(friendId: String): Flow<Friend?> {
        val friendStream = friendDao.getFriendByIdStream(friendId)
        return friendStream.map { friendEntity ->
            val locationEntity = locationDao.getLocationByFriendIdStream(friendEntity.id)
            Friend(
                id = friendEntity.id,
                name = friendEntity.name,
                username = friendEntity.username,
                theirShareId = friendEntity.theirShareId,
                userShareId = friendEntity.userShareId,
                location = locationEntity
            )
        }
    }

    override suspend fun addShare(data: CreateShareRequest): APIResult<Unit> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(context)
        if (accessToken == null) {
            return APIResult.Error(
                APIError.CustomError(
                    "No access token found",
                    401,
                    "The user has to be logged in to share location"
                )
            )
        }
        val shareResponse = apiCall {
            api.createShare(
                url = appendToServerUrl(serverUrl, ApiPath.SHARES),
                token = accessToken,
                body = data
            )
        }
        return when (shareResponse) {
            is APIResult.Success -> {
                getAllUsersOfServer()
                APIResult.Success(Unit)
            }

            is APIResult.Error -> {
                APIResult.Error(exception = shareResponse.exception)
            }
        }
    }

    override suspend fun removeShare(shareId: String): APIResult<Unit> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(context)
        if (accessToken == null) {
            return APIResult.Error(
                APIError.CustomError(
                    "No access token found",
                    401,
                    "The user has to be logged in to remove a share"
                )
            )
        }

        val deleteResponse = apiCall {
            api.deleteShare(
                url = appendToServerUrl(serverUrl, ApiPath.SHARES),
                token = accessToken,
                id = shareId
            )
        }

        return when (deleteResponse) {
            is APIResult.Success -> {
                getAllUsersOfServer()
                APIResult.Success(Unit)
            }

            is APIResult.Error -> {
                APIResult.Error(exception = deleteResponse.exception)
            }
        }
    }

    override suspend fun sendWakeUp(friendId: String): APIResult<Unit> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(context)
        if (accessToken == null) {
            return APIResult.Error(
                APIError.CustomError(
                    "No access token found",
                    401,
                    "The user has to be logged in to send a wake up"
                )
            )
        }

        return apiCall {
            api.wakeUp(
                url = appendToServerUrl(serverUrl, ApiPath.WAKE_UP),
                token = accessToken,
                body = WakeUpRequest(
                    userId = friendId
                )
            )
        }
    }

    override suspend fun syncFriends(): APIResult<Unit> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(context)
        if (accessToken == null) {
            return APIResult.Error(
                APIError.CustomError(
                    "No access token found",
                    401,
                    "The user has to be logged in to get all shares"
                )
            )
        }

        val user = Storage(context).getUser()

        val allShares = apiCall {
            api.getExistingShares(
                url = appendToServerUrl(serverUrl, ApiPath.SHARES),
                token = accessToken
            )
        }

        return when (allShares) {
            is APIResult.Success -> {
                when (val allUsers = getAllUsersOfServer()) {
                    is APIResult.Success -> {
                        Log.d("FriendRepositoryImpl", "All users: ${allUsers.data}")
                        Log.d("FriendRepositoryImpl", "All shares: ${allShares.data}")
                        Log.d("FriendRepositoryImpl", "User: ${user?.id}")

                        val (friendsToKeep, friendsToDelete) = allUsers.data.partition {
                            Log.d(
                                "FriendRepositoryImpl",
                                it.id + " vs " + user?.id + " = " + (it.id == user?.id)
                            )
                            allShares.data.any { share ->
                                (share.shared_by == it.id && share.shared_with == user?.id) || (share.shared_with == it.id && share.shared_by == user?.id)
                            } && it.id != user?.id
                        }

                        friendsToKeep.map { friend ->
                            Log.d("FriendRepositoryImpl", "Friend: $friend")
                            FriendEntity(
                                id = friend.id,
                                name = friend.name,
                                username = friend.username,
                                theirShareId = allShares.data.find { share ->
                                    share.shared_by == friend.id
                                }?.id,
                                userShareId = allShares.data.find { share ->
                                    share.shared_with == friend.id
                                }?.id
                            )
                        }.forEach {
                            friendDao.upsert(it)
                        }

                        friendsToDelete.forEach { friend ->
                            friendDao.delete(friend.id)
                        }

                        APIResult.Success(Unit)
                    }

                    is APIResult.Error -> {
                        Log.e(
                            "FriendRepositoryImpl",
                            "Error syncing friends: ${allUsers.exception}"
                        )
                        APIResult.Error(exception = allUsers.exception)
                    }
                }
            }

            is APIResult.Error -> {
                Log.e("FriendRepositoryImpl", "Error syncing friends: ${allShares.exception}")
                APIResult.Error(exception = allShares.exception)
            }
        }
    }

    override suspend fun syncFriendsLocations(): APIResult<Unit> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(context)
        if (accessToken == null) {
            return APIResult.Error(
                APIError.CustomError(
                    "No access token found",
                    401,
                    "The user has to be logged in to get the shared locations"
                )
            )
        }

        val locationsResponse = apiCall {
            api.getSharedLocations(
                appendToServerUrl(serverUrl, ApiPath.SHARED_LOCATIONS),
                accessToken
            )
        }

        return when (locationsResponse) {
            is APIResult.Success -> {
                locationsResponse.data.forEach {
                    locationDao.deleteLocation(it)
                    locationDao.insertLocation(it)
                }
                APIResult.Success(Unit)
            }

            is APIResult.Error -> {
                APIResult.Error(exception = locationsResponse.exception)
            }
        }
    }

    override suspend fun listenForUpdates() {
        TODO("Not yet implemented")
    }

    override suspend fun getAllUsersOfServer(): APIResult<List<CreateUserResponse>> {
        val (accessToken, serverUrl) = getAccessTokenAndServerUrl(context)
        if (accessToken == null) {
            return APIResult.Error(
                APIError.CustomError(
                    "No access token found",
                    401,
                    "The user has to be logged in to get all users"
                )
            )
        }

        return apiCall {
            api.getUsersOfServer(
                url = appendToServerUrl(serverUrl, ApiPath.USERS),
                token = accessToken
            )
        }
    }
}