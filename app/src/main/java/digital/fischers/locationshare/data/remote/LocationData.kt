package digital.fischers.locationshare.data.remote

data class LocationData(
    val name: String,
    val location: String,
    val velocity: String,
    val more: String? = null
)
