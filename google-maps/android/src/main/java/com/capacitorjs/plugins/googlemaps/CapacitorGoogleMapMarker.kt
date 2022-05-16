package com.capacitorjs.plugins.googlemaps

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import org.json.JSONObject


class CapacitorGoogleMapMarker(fromJSONObject: JSONObject): ClusterItem {
    var coordinate: LatLng = LatLng(0.0, 0.0)
    var opacity: Float = 1.0f
    private var title: String
    private var snippet: String
    var isFlat: Boolean = false
    var iconUrl: String? = null
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

    fun getMarkerOptions(): MarkerOptions {
        val markerOptions = MarkerOptions()
        markerOptions.position(coordinate)
        markerOptions.title(title)
        markerOptions.snippet(snippet)
        markerOptions.alpha(opacity)
        markerOptions.flat(isFlat)
        markerOptions.draggable(draggable)

        return markerOptions
    }
}