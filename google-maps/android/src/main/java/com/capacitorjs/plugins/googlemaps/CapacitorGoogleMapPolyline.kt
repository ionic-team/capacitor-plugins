package com.capacitorjs.plugins.googlemaps

import android.graphics.Color
import androidx.core.graphics.toColor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import org.json.JSONObject

class CapacitorGoogleMapPolyline(fromJSONObject: JSONObject) {
    var path: MutableList<LatLng> = mutableListOf<LatLng>()
    var strokeWidth: Float = 1.0f
    var strokeColor: Color = Color.BLUE.toColor()
    var clickable: Boolean
    var zIndex: Float = 0.00
    var googleMapsPolyline: Polyline? = null

    init {
        if (!fromJSONObject.has("path")) {
            throw InvalidArgumentsError("Polyline object is missing the required 'path' property")
        }

        val pathArray = fromJSONObject.getJSONArray("path")

        for (i in 1..pathArray.length()) {
            val obj = pathArray.getJSONObject(i)
            if (!obj.has("lat") || !obj.has("lng")) {
                throw InvalidArgumentsError("LatLng object is missing the required 'lat' and/or 'lng' property")
            }

            val lat = obj.getDouble("lat")
            val lng = obj.getDouble("lng")

            path.add(LatLng(lat, lng))
        }

        strokeColor = Color.parseColor(fromJSONObject.getString("strokeColor")).toColor()
        strokeWidth = fromJSONObject.optDouble("strokeWidth", 1.0).toFloat()
        clickable = fromJSONObject.optBoolean("tappable", false)
        zIndex = fromJSONObject.optDouble("zIndex", 0.00).toFloat()
    }

}