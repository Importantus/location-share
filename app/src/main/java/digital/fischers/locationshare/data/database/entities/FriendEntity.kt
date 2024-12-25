package digital.fischers.locationshare.data.database.entities

import androidx.room.Entity

@Entity
data class FriendEntity(
    val id: String,
    val name: String,
    val username: String,
    val theirShareId: String?,
    val userShareId: String?,
)
