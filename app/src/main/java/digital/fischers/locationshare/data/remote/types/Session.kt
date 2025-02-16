package digital.fischers.locationshare.data.remote.types

import java.util.Date

data class CreateSessionRequest(
    val name: String,
    val username: String,
    val password: String,
    val read_only: Boolean = false,
    val writing: Boolean = true
)

data class CreateSessionResponse(
    val id: String,
    val token: String,
    val user: CreateUserResponse
)

data class Session(
    val id: String,
    val name: String,
    val writing: Boolean,
    val read_only: Boolean,
    val created_at: Date,
    val updated_at: Date
)