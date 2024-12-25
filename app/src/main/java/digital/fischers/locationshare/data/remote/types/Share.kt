package digital.fischers.locationshare.data.remote.types

data class Share(
    val id: String,
    val valid_until: Long,
    val shared_with: String,
    val shared_by: String,
    val created_at: Long,
    val updated_at: Long
)

data class CreateShareRequest(
    val valid_until: Long?,
    val shared_with: List<String>
)
