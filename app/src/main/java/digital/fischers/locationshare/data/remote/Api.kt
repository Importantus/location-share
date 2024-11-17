package digital.fischers.locationshare.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LocationApi {
    @POST("locations")
    suspend fun sendLocation(@Body location: LocationData): Response<Void>
}