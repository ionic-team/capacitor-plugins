package com.capacitorjs.plugins.googlemaps

import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import org.json.JSONObject

class CapacitorGoogleMapsTileOverlay(fromJSONObject: JSONObject) {
    var imageBounds: LatLngBounds
    var imageSrc: String? = null
    var opacity: Float = 1.0f
    var zIndex: Float = 0.0f
    var visible: Boolean = true
    var googleMapsTileOverlay: GroundOverlay? = null

    init {
        val latLngObj = fromJSONObject.getJSONObject("imageBounds")
        val north = latLngObj.optDouble("north", 0.0)
        val south = latLngObj.optDouble("south", 0.0)
        val east = latLngObj.optDouble("east", 0.0)
        val west = latLngObj.optDouble("west", 0.0)

        imageBounds = LatLngBounds(LatLng(south, west), LatLng(north, east))
        imageSrc = fromJSONObject.optString("imageSrc", null)
        zIndex = fromJSONObject.optLong("zIndex", 0).toFloat()
        visible = fromJSONObject.optBoolean("visible", true)
        opacity = 1.0f - fromJSONObject.optDouble("opacity", 1.0).toFloat()
    }
}