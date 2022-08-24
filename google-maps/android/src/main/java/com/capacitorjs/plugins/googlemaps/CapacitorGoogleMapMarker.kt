package com.capacitorjs.plugins.googlemaps

import android.util.Size
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterItem
import org.json.JSONObject


class CapacitorGoogleMapMarker(fromJSONObject: JSONObject): ClusterItem {
    var coordinate: LatLng = LatLng(0.0, 0.0)
    var opacity: Float = 1.0f
    private var title: String
    private var snippet: String
    var isFlat: Boolean = false
    var iconUrl: String? = null
    var iconSize: Size? = null
    var iconAnchor: CapacitorGoogleMapsPoint? = null
    var draggable: Boolean = false
    var googleMapMarker: Marker? = null

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

        draggable = fromJSONObject.optBoolean("draggable", false)
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

    private fun buildIconAnchorPoint(iconAnchor: CapacitorGoogleMapsPoint): CapacitorGoogleMapsPoint? {
        iconSize ?: return null

        val u: Float = iconAnchor.x / iconSize!!.width
        val v: Float = iconAnchor.y / iconSize!!.height

        return CapacitorGoogleMapsPoint(u, v)
    }
}