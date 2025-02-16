package digital.fischers.locationshare.domain.repositories

interface WebsocketRepository {
    suspend fun connect()
    fun disconnect()
}