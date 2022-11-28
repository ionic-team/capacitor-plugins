package com.capacitorjs.plugins.googlemaps
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import com.google.android.gms.maps.model.*

class CapacitorGoogleMapsPolyline(fromJSONObject: JSONObject) {
    var coordinate1: LatLng? = null
    var coordinate2:LatLng? = null
    var width: Float = 10F
    var color: Int = 1;
    var googleMapPolyline:Polyline? = null;

    init {

        if (fromJSONObject.has("width")) {
            width = fromJSONObject.get("width") as Float
        }

        if (fromJSONObject.has("color")) {
            color = fromJSONObject.getInt("color")
        }

        if (fromJSONObject.has("coordinate1")) {
            val coordinate1JSONObject = fromJSONObject.getJSONObject("coordinate1")
            if(!coordinate1JSONObject.has("lat") || !coordinate1JSONObject.has("lng")) {
                throw InvalidArgumentsError("LatLng object is missing the required 'lat' and/or 'lng' property")
            }

            val lat = coordinate1JSONObject.getDouble("lat")
            val lng = coordinate1JSONObject.getDouble("lng")
            coordinate1 = LatLng(lat, lng)
        } else {
            coordinate1 = null
        }


        if (fromJSONObject.has("coordinate2")) {
            val coordinate2JSONObject = fromJSONObject.getJSONObject("coordinate1")
            if(!coordinate2JSONObject.has("lat") || !coordinate2JSONObject.has("lng")) {
                throw InvalidArgumentsError("LatLng object is missing the required 'lat' and/or 'lng' property")
            }

            val lat = coordinate2JSONObject.getDouble("lat")
            val lng = coordinate2JSONObject.getDouble("lng")
            coordinate2 = LatLng(lat, lng)
        } else {
            coordinate2 = null
        }
    }

}