package com.capacitorjs.plugins.googlemaps

import android.graphics.Color
import android.util.Size
import androidx.core.math.MathUtils
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterItem
import org.json.JSONObject


class CapacitorGoogleMapMarker(fromJSONObject: JSONObject): ClusterItem {
    var coordinate: LatLng = LatLng(0.0, 0.0)
    var opacity: Float = 1.0f
    private var title: String
    private var snippet: String
    private var zIndex: Float? = null
    var isFlat: Boolean = false
    var iconUrl: String? = null
    var iconSize: Size? = null
    var iconAnchor: CapacitorGoogleMapsPoint? = null
    var draggable: Boolean = false
    var googleMapMarker: Marker? = null
    var colorHue: Float? = null
    var markerOptions: MarkerOptions? = null

    init {
        if(!fromJSONObject.has("coordinate")) {
            throw InvalidArgumentsError("Marker object is missing the required 'coordinate' property")
        }

        val latLngObj = fromJSONObject.getJSONObject("coordinate")
        if (!latLngObj.has("lat") || !latLngObj.has("lng")) {
            throw InvalidArgumentsError("LatLng object is missing the required 'lat' and/or 'lng' property")
        }

        coordinate = LatLng(latLngObj.getDouble("lat"), latLngObj.getDouble("lng"))
        title = fromJSONObject.optString("title")
        opacity = fromJSONObject.optDouble("opacity", 1.0).toFloat()
        snippet = fromJSONObject.optString("snippet")
        isFlat = fromJSONObject.optBoolean("isFlat", false)
        iconUrl = fromJSONObject.optString("iconUrl")
        if (fromJSONObject.has("iconSize")) {
            val iconSizeObject = fromJSONObject.getJSONObject("iconSize")
            iconSize = Size(iconSizeObject.optInt("width", 0), iconSizeObject.optInt("height", 0))
        }

        if (fromJSONObject.has("iconAnchor")) {
            val inputAnchorPoint = CapacitorGoogleMapsPoint(fromJSONObject.getJSONObject("iconAnchor"))
            iconAnchor = this.buildIconAnchorPoint(inputAnchorPoint)
        }

        if(fromJSONObject.has("tintColor")) {
            val tintColorObject = fromJSONObject.getJSONObject("tintColor")

            val r = MathUtils.clamp(tintColorObject.optDouble("r", 0.00), 0.00, 255.0)
            val g = MathUtils.clamp(tintColorObject.optDouble("g", 0.00), 0.00, 255.0)
            val b = MathUtils.clamp(tintColorObject.optDouble("b", 0.00), 0.00, 255.0)

            val hsl = FloatArray(3)
            Color.RGBToHSV(r.toInt(), g.toInt(), b.toInt(), hsl)

            colorHue = hsl[0]
        }

        draggable = fromJSONObject.optBoolean("draggable", false)
        zIndex = fromJSONObject.optLong("zIndex").toFloat()
    }

    override fun getPosition(): LatLng {
        return LatLng(coordinate.latitude, coordinate.longitude)
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }

    override fun getZIndex(): Float? {
        return zIndex
    }

    private fun buildIconAnchorPoint(iconAnchor: CapacitorGoogleMapsPoint): CapacitorGoogleMapsPoint? {
        iconSize ?: return null

        val u: Float = iconAnchor.x / iconSize!!.width
        val v: Float = iconAnchor.y / iconSize!!.height

        return CapacitorGoogleMapsPoint(u, v)
    }
}