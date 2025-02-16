package digital.fischers.locationshare.data.remote.types

import java.util.Date

data class Share(
    val id: String,
    val valid_until: Long,
    val shared_with: String,
    val shared_by: String,
    val created_at: Date,
    val updated_at: Date
)

data class CreateShareRequest(
    val valid_until: Long?,
    val shared_with: List<String>
)
