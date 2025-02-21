package digital.fischers.locationshare.data.remote

import digital.fischers.locationshare.data.database.entities.LocationEntity
import digital.fischers.locationshare.data.remote.types.CreateSessionRequest
import digital.fischers.locationshare.data.remote.types.CreateSessionResponse
import digital.fischers.locationshare.data.remote.types.CreateShareRequest
import digital.fischers.locationshare.data.remote.types.CreateUserRequest
import digital.fischers.locationshare.data.remote.types.CreateUserResponse
import digital.fischers.locationshare.data.remote.types.Info
import digital.fischers.locationshare.data.remote.types.RegisterFCMTokenRequest
import digital.fischers.locationshare.data.remote.types.Session
import digital.fischers.locationshare.data.remote.types.Share
import digital.fischers.locationshare.data.remote.types.UpdateUserRequest
import digital.fischers.locationshare.data.remote.types.WakeUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface LocationApi {
    @POST
    suspend fun createUser(
        @Url url: String,
        @Body body: CreateUserRequest
    ): Response<CreateUserResponse>

    @GET
    suspend fun getUsersOfServer(
        @Url url: String,
        @Header("Authorization") token: String,
        @Query("id") userId: String? = null
    ): Response<List<CreateUserResponse>>

    @DELETE
    suspend fun deleteUser(@Url url: String, @Header("Authorization") token: String): Response<Unit>

    @PUT
    suspend fun updateUser(
        @Url url: String,
        @Header("Authorization") token: String,
        @Body body: UpdateUserRequest
    ): Response<CreateUserResponse>

    @POST
    suspend fun createSession(
        @Url url: String,
        @Body body: CreateSessionRequest
    ): Response<CreateSessionResponse>

    @GET
    suspend fun getAllSessions(
        @Url url: String,
        @Header("Authorization") token: String
    ): Response<List<Session>>

    @DELETE
    suspend fun deleteSession(
        @Url url: String,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST
    suspend fun createShare(
        @Url url: String,
        @Header("Authorization") token: String,
        @Body body: CreateShareRequest
    ): Response<Unit>

    @GET
    suspend fun getExistingShares(
        @Url url: String,
        @Header("Authorization") token: String
    ): Response<List<Share>>

    @DELETE
    suspend fun deleteShare(
        @Url url: String,
        @Header("Authorization") token: String,
        @Query("id") id: String
    ): Response<Unit>

    @POST
    suspend fun createLocation(
        @Url url: String,
        @Header("Authorization") token: String,
        @Body body: List<LocationEntity>
    ): Response<Unit>

    @GET
    suspend fun getLocations(
        @Url url: String,
        @Header("Authorization") token: String,
        @Query("from") since: Long,
        @Query("to") until: Long
    ): Response<List<LocationEntity>>

    @DELETE
    suspend fun deleteLocation(
        @Url url: String,
        @Header("Authorization") token: String,
        @Query("from") since: Long,
        @Query("to") until: Long
    ): Response<Unit>

    @GET
    suspend fun getSharedLocations(
        @Url url: String,
        @Header("Authorization") token: String,
    ): Response<List<LocationEntity>>

    @GET
    suspend fun getInfo(
        @Url url: String
    ): Response<Info>

    @POST
    suspend fun registerFCMToken(
        @Url url: String,
        @Header("Authorization") token: String,
        @Body body: RegisterFCMTokenRequest
    ): Response<Unit>

    @POST
    suspend fun wakeUp(
        @Url url: String,
        @Header("Authorization") token: String,
        @Body body: WakeUpRequest
    ): Response<Unit>
}