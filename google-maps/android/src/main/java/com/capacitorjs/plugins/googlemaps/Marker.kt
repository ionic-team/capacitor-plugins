package com.capacitorjs.plugins.googlemaps

import com.google.android.libraries.maps.model.LatLng
import org.json.JSONObject

class Marker(fromJSONObject: JSONObject) {
    var coordinate: LatLng = LatLng(0.0, 0.0)
    var opacity: Float = 1.0f
    var title: String? = null
    var snippet: String? = null
    var isFlat: Boolean = false
    var iconUrl: String? = null
    var draggable: Boolean = false

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
}