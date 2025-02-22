package digital.fischers.locationshare.ui.friend

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.locationshare.data.remote.APIError
import digital.fischers.locationshare.data.remote.APIResult
import digital.fischers.locationshare.domain.repositories.FriendRepository
import digital.fischers.locationshare.domain.types.FriendUIState
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
) : ViewModel() {
    var refreshLoading by mutableStateOf(false)
        private set

    var removeShareLoading by mutableStateOf(false)
        private set

    var refreshLoadingError: APIError? by mutableStateOf(null)
        private set

    var removeShareLoadingError: APIError? by mutableStateOf(null)
        private set

    suspend fun refreshFriend(id: String) {
        refreshLoading = true
        when (val response = friendRepository.sendWakeUp(id)) {
            is APIResult.Success -> {
                Log.d("FriendViewModel", "Refresh successful")
            }

            is APIResult.Error -> {
                refreshLoadingError = response.exception
            }
        }
        refreshLoading = false
    }

    suspend fun stopSharing(friendUIState: FriendUIState) {
        removeShareLoading = true
        when (val response = friendUIState.userShareId?.let { friendRepository.removeShare(it) }) {
            is APIResult.Success -> {
                Log.d("FriendViewModel", "Remove share successful")
            }

            is APIResult.Error -> {
                removeShareLoadingError = response.exception
            }

            null -> {
                Log.d("FriendViewModel", "User share id is null")
            }
        }
        removeShareLoading = false
    }

    fun clearErrors() {
        refreshLoadingError = null
        removeShareLoadingError = null
    }
}