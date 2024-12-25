package digital.fischers.locationshare.data.keyValueStorage.entities

import com.google.gson.Gson

data class UserEntity(
    val id: String,
    val name: String,
    val username: String,
    val email: String,
    val authToken: String,
    val sessionId: String
) {
    fun toJSON(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromJSONOrNull(json: String): UserEntity? {
            return try {
                Gson().fromJson(json, UserEntity::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}
