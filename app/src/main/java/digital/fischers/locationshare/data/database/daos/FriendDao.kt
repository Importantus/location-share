package digital.fischers.locationshare.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import digital.fischers.locationshare.data.database.entities.FriendEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {
    @Query("SELECT * FROM FriendEntity")
    fun getFriendsStream(): Flow<List<FriendEntity>>

    @Query("SELECT * FROM FriendEntity")
    fun getFriends(): List<FriendEntity>

    @Query("SELECT * FROM FriendEntity WHERE id = :id")
    fun getFriendByIdStream(id: String): Flow<FriendEntity>

    @Update
    suspend fun update(friend: FriendEntity)

    @Insert
    suspend fun insert(friend: FriendEntity)

    @Upsert
    suspend fun upsert(friend: FriendEntity)

    @Query("DELETE FROM FriendEntity WHERE id = :id")
    suspend fun delete(id: String)
}