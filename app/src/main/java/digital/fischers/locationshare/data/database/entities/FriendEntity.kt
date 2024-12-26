package digital.fischers.locationshare.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FriendEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val username: String,
    val theirShareId: String?,
    val userShareId: String?,
)
