package digital.fischers.locationshare.domain.types

import digital.fischers.locationshare.data.database.entities.LocationEntity
import kotlinx.coroutines.flow.Flow

data class Friend(
    val id: String,
    val name: String,
    val username: String,
    val theirShareId: String?,
    val userShareId: String?,
    val location: Flow<LocationEntity?>?
)
