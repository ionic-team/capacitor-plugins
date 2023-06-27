package com.capacitorjs.plugins.googlemaps

import android.graphics.Color
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

class CapacitorGoogleMapsCircle(fromJSONObject: JSONObject) {
    var center: LatLng
    var radius: Float
    var strokeWidth: Float = 1.0f
    var strokeColor: Int = Color.BLUE
    var fillColor: Int = Color.BLUE
    var clickable: Boolean
    var zIndex: Float = 0.00f
    var tag: String = ""
    var googleMapsCircle: Circle? = null

    init {
        if (!fromJSONObject.has("center")) {
            throw InvalidArgumentsError("Circle object is missing the required 'center' property")
        }

        if (!fromJSONObject.has("radius")) {
            throw InvalidArgumentsError("Circle object is missing the required 'radius' property")
        }

        val latLng = fromJSONObject.getJSONObject("center")
        if (!latLng.has("lat") || !latLng.has("lng")) {
            throw InvalidArgumentsError("LatLng object is missing the required 'lat' and/or 'lng' property")
        }

        val lat = latLng.getDouble("lat")
        val lng = latLng.getDouble("lng")

        center = LatLng(lat, lng)

        radius = fromJSONObject.getDouble("radius").toFloat()

        val strokeOpacity = fromJSONObject.optDouble("strokeOpacity", 1.0)
        strokeColor = this.processColor(fromJSONObject.getString("strokeColor"), strokeOpacity)

        val fillOpacity = fromJSONObject.optDouble("fillOpacity", 1.0)
        fillColor = this.processColor(fromJSONObject.getString("fillColor"), fillOpacity)

        strokeWidth = fromJSONObject.optDouble("strokeWeight", 1.0).toFloat()
        clickable = fromJSONObject.optBoolean("clickable", false)
        zIndex = fromJSONObject.optDouble("zIndex", 1.0).toFloat()
        tag = fromJSONObject.optString("tag", "")
    }


    private fun processColor(hex: String, opacity: Double): Int {
        val colorInt = Color.parseColor(hex)

        val alpha = (opacity * 255.0).toInt()
        val red = android.graphics.Color.red(colorInt)
        val green = android.graphics.Color.green(colorInt)
        val blue = android.graphics.Color.blue(colorInt)

        return Color.argb(alpha, red, green, blue)
    }
}