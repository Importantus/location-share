package digital.fischers.locationshare.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.locationshare.domain.repositories.FriendRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
) : ViewModel() {
    suspend fun syncFriends() {
        friendRepository.syncFriends()
    }

    suspend fun syncFriendLocations() {
        friendRepository.syncFriendsLocations()
    }
}