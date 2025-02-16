package digital.fischers.locationshare.data.repositories

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import digital.fischers.locationshare.data.database.daos.LocationDao
import digital.fischers.locationshare.data.database.entities.LocationEntity
import digital.fischers.locationshare.data.keyValueStorage.Storage
import digital.fischers.locationshare.domain.repositories.FriendRepository
import digital.fischers.locationshare.domain.repositories.WebsocketRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.net.URL
import javax.inject.Inject
import kotlin.math.min
import kotlin.math.pow

const val TAG = "WS"

class WebSocketRepositoryImpl @Inject constructor(
    private val client: OkHttpClient,
    val context: Context,
    private val friendRepository: FriendRepository,
    val locationDao: LocationDao
) : WebsocketRepository {

    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Reconnect-Parameter
    private var retryCount = 0
    private val maxRetryDelay = 60_000L
    private var isConnected = false

    private suspend fun getWsUrl(): String {
        val url = Storage(context).getServerUrl()

        // Get the baseUrl and return it in the form of wss://<base_url>/v1/ws
        val host = URL(url).host
        val port = URL(url).port
        return if (port != -1) "ws://$host:$port/v1/ws" else "wss://$host/v1/ws"
    }

    override suspend fun connect() {
        val wsUrl = getWsUrl()
        val accessToken = Storage(context).getUser()?.authToken ?: return

        val request = Request.Builder()
            .url(wsUrl)
            .addHeader("Authorization", accessToken)
            .build()

        webSocket = client.newWebSocket(request, webSocketListener)
    }

    override fun disconnect() {
        isConnected = false
        webSocket?.close(1000, "Closing")
        webSocket = null
    }

    private suspend fun parseMessage(text: String) {
        try {
            val jsonObj = JsonParser.parseString(text).asJsonObject
            val type = jsonObj.get("type").asString
            val payload = jsonObj.get("data")

            when(type) {
                "location_update" -> {
                    val location = Gson().fromJson(payload, LocationEntity::class.java)
                    locationDao.upsertLocation(location)
                }
                "share_create", "share_delete" -> {
                    friendRepository.syncFriends()
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error while parsing a websocket message: $ex")
        }
    }

    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            retryCount = 0
            isConnected = true
            Log.d(TAG, "Websocket connected")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "Received message: $text")

            scope.launch { parseMessage(text) }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            isConnected = false

            reconnectWithBackoff()
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket closes: $reason")
            isConnected = false
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG,"WebSocket closed: $reason")
            isConnected = false
        }
    }

    private fun reconnectWithBackoff() {
        if (!isConnected) {
            val delay = min(1000L * (2.0.pow(retryCount)).toLong(), maxRetryDelay)
            Log.d(TAG, "Retry websocket connection in ${delay / 1000} seconds...")

            scope.launch {
                delay(delay)
                retryCount++
                connect()
            }
        }
    }
}