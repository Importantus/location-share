package digital.fischers.locationshare.ui.map.utils

import org.maplibre.android.geometry.LatLng
import kotlin.math.cos
import kotlin.math.sin

fun createCircleFromPolygon(center: LatLng, radiusInMeter: Float): List<LatLng> {
    val numberOfSides = 100
    val vertices = mutableListOf<LatLng>()

    for (i in 0 until numberOfSides) {
        val angle = Math.toRadians(i.toDouble() * 360 / numberOfSides)
        val dx = radiusInMeter * cos(angle)
        val dy = radiusInMeter * sin(angle)
        val lat = center.latitude + (180 / Math.PI) * (dy / 6378137)
        val lon =
            center.longitude + (180 / Math.PI) * (dx / 6378137) / cos(center.latitude * Math.PI / 180)
        vertices.add(LatLng(lat, lon))
    }

    return vertices
}