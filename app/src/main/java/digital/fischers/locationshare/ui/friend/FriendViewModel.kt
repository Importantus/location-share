package digital.fischers.locationshare.ui.friend

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.locationshare.domain.repositories.FriendRepository
import digital.fischers.locationshare.domain.repositories.UserRepository
import digital.fischers.locationshare.domain.types.FriendUIState
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    var refreshLoading by mutableStateOf(false)
        private set

    var removeShareLoading by mutableStateOf(false)
        private set

    suspend fun refreshFriend(id: String) {
        refreshLoading = true
        friendRepository.sendWakeUp(id)
        refreshLoading = false
    }

    suspend fun stopSharing(friendUIState: FriendUIState) {
        removeShareLoading = true
        friendUIState.userShareId?.let { friendRepository.removeShare(it) }
        removeShareLoading = false
    }
}