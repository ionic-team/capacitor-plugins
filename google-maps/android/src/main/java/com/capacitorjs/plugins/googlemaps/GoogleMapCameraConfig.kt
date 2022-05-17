package com.capacitorjs.plugins.googlemaps

import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

class GoogleMapCameraConfig(fromJSONObject: JSONObject) {
    var coordinate: LatLng? = null
    var zoom: Double? = null
    var angle: Double? = null
    var bearing: Double? = null
    var animate: Boolean? = null
    var animationDuration: Double? = null

    init {
        if (fromJSONObject.has("zoom")) {
            zoom = fromJSONObject.getDouble("zoom")
        }

        if(fromJSONObject.has("angle")) {
            angle = fromJSONObject.getDouble("angle")
        }

        if (fromJSONObject.has("bearing")) {
            bearing = fromJSONObject.getDouble("bearing")
        }

        if (fromJSONObject.has("animate")) {
            animate = fromJSONObject.getBoolean("animate")
        }

        if (fromJSONObject.has("animationDuration")) {
            animationDuration = fromJSONObject.getDouble("animationDuration")
        }

        if (fromJSONObject.has("coordinate")) {
            val coordinateJSONObject = fromJSONObject.getJSONObject("coordinate")
            if(!coordinateJSONObject.has("lat") || !coordinateJSONObject.has("lng")) {
                throw InvalidArgumentsError("LatLng object is missing the required 'lat' and/or 'lng' property")
            }

            val lat = coordinateJSONObject.getDouble("lat")
            val lng = coordinateJSONObject.getDouble("lng")
            coordinate = LatLng(lat, lng)
        } else {
            coordinate = null
        }
    }


}