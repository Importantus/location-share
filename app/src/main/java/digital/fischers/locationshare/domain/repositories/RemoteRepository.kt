package digital.fischers.locationshare.domain.repositories

interface RemoteRepository {
    suspend fun sendLocationData()
}