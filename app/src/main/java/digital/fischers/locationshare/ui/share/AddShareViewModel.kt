package digital.fischers.locationshare.ui.share

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.locationshare.data.remote.APIError
import digital.fischers.locationshare.data.remote.APIResult
import digital.fischers.locationshare.data.remote.types.CreateShareRequest
import digital.fischers.locationshare.data.remote.types.CreateUserResponse
import digital.fischers.locationshare.domain.repositories.FriendRepository
import digital.fischers.locationshare.domain.repositories.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@HiltViewModel
class AddShareViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    var usersLoading by mutableStateOf(false)
        private set

    var usersLoadError: APIError? by mutableStateOf(null)
        private set

    var users by mutableStateOf(emptyList<CreateUserResponse>())
        private set

    var createShareLoading by mutableStateOf(false)
        private set

    var createShareError: APIError? by mutableStateOf(null)
        private set

    var hoursToShare: Int by mutableIntStateOf(1)
        private set

    var share by mutableStateOf(
        CreateShareRequest(
            valid_until = null,
            shared_with = emptyList()
        )
    )
        private set

    suspend fun getAllUsersFromServer() {
        usersLoading = true
        usersLoadError = null
        when (val result = friendRepository.getAllUsersOfServer()) {
            is APIResult.Success -> {
                val friends = friendRepository.getFriendsStream().firstOrNull()
                users =
                    result.data.filter {
                        it.id != userRepository.getUser()?.id
                                && friends?.none { friend ->
                            friend.id == it.id && friend.userShareId != null
                        } == true
                    }
            }

            is APIResult.Error -> {
                usersLoadError = result.exception
            }
        }
        usersLoading = false
    }

    suspend fun createShare() {
        createShareLoading = true
        createShareError = null

        if (share.valid_until != null) {
            share = share.copy(
                valid_until = System.currentTimeMillis() + (hoursToShare!! * 60 * 60 * 1000)
            )
        }

        when (val result = friendRepository.addShare(share)) {
            is APIResult.Success -> {
                createShareLoading = false
            }

            is APIResult.Error -> {
                createShareError = result.exception
                createShareLoading = false
            }
        }
    }

    fun onSelectUser(userId: String) {
        share = share.copy(
            shared_with = share.shared_with.filter { it != userId } + userId
        )
    }

    fun onDeselectUser(userId: String) {
        share = share.copy(
            shared_with = share.shared_with.filter { it != userId }
        )
    }

    fun increaseHoursToShare() {
        hoursToShare++
    }

    fun decreaseHoursToShare() {
        if (hoursToShare > 1) {
            hoursToShare--
        }
    }

    fun setToExpire() {
        share = share.copy(
            valid_until = System.currentTimeMillis()
        )
    }

    fun setNeverExpire() {
        share = share.copy(
            valid_until = null
        )
    }
}