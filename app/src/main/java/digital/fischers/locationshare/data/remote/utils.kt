package digital.fischers.locationshare.data.remote

import android.content.Context
import digital.fischers.locationshare.data.keyValueStorage.Storage

suspend fun getAccessTokenAndServerUrl(context: Context): Pair<String?, String> {
    val accessToken = Storage(context).getUser()?.authToken
    val serverUrl = Storage(context).getServerUrl()

    return Pair(accessToken, serverUrl)
}

fun appendToServerUrl(serverUrl: String, path: ApiPath): String {
    return "${serverUrl.removeSuffix("/")}/${path.path.removePrefix("/")}"
}