package digital.fischers.locationshare.data.remote.types

data class CreateUserRequest(
    val name: String,
    val email: String,
    val username: String,
    val password: String,
    val registration_secret: String? = null
)

data class CreateUserResponse(
    val id: String,
    val name: String,
    val email: String,
    val username: String
)

data class UpdateUserRequest(
    val name: String,
    val email: String,
    val username: String
)

