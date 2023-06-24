package com.capacitorjs.plugins.googlemaps

import android.graphics.Color
import androidx.core.graphics.toColor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import org.json.JSONObject

class CapacitorGoogleMapPolyline(fromJSONObject: JSONObject) {
    var path: MutableList<LatLng> = mutableListOf<LatLng>()
    var styleSpans: MutableList<CapacitorGoogleMapsStyleSpan> = mutableListOf<CapacitorGoogleMapsStyleSpan>()
    var strokeWidth: Float = 1.0f
    var strokeColor: Int = Color.BLUE
    var clickable: Boolean
    var geodesic: Boolean
    var zIndex: Float = 0.00f
    var tag: String = ""
    var googleMapsPolyline: Polyline? = null

    init {
        if (!fromJSONObject.has("path")) {
            throw InvalidArgumentsError("Polyline object is missing the required 'path' property")
        }

        val pathArray = fromJSONObject.getJSONArray("path")

        for (i in 0 until pathArray.length()) {
            val obj = pathArray.getJSONObject(i)
            if (!obj.has("lat") || !obj.has("lng")) {
                throw InvalidArgumentsError("LatLng object is missing the required 'lat' and/or 'lng' property")
            }

            val lat = obj.getDouble("lat")
            val lng = obj.getDouble("lng")

            path.add(LatLng(lat, lng))
        }

        if (fromJSONObject.has("styleSpans")) {
            val styleSpanArray = fromJSONObject.getJSONArray("styleSpans")
            for (i in 0 until styleSpanArray.length()) {
                val obj = styleSpanArray.getJSONObject(i)

                if (obj.has("color")) {
                    val color = obj.getString("color")

                    if (obj.has("segments")) {
                        val segments = obj.getDouble("segments")
                        styleSpans.add(CapacitorGoogleMapsStyleSpan(Color.parseColor(color), segments))
                    } else {
                        styleSpans.add(CapacitorGoogleMapsStyleSpan(Color.parseColor(color), null))
                    }
                }
            }
        }

        val strokeOpacity = fromJSONObject.optDouble("strokeOpacity", 1.0)

        strokeColor = this.processColor(fromJSONObject.getString("strokeColor"), strokeOpacity)
        strokeWidth = fromJSONObject.optDouble("strokeWeight", 1.0).toFloat()
        clickable = fromJSONObject.optBoolean("clickable", false)
        geodesic = fromJSONObject.optBoolean("geodesic", false)
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