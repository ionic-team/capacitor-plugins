package com.capacitorjs.plugins.googlemaps

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import org.json.JSONArray
import org.json.JSONObject

class CapacitorGoogleMapsPolygon(fromJSONObject: JSONObject) {
    var shapes: MutableList<MutableList<LatLng>> = mutableListOf<MutableList<LatLng>>()
    var strokeWidth: Float = 1.0f
    var strokeColor: Int = Color.BLUE
    var fillColor: Int = Color.BLUE
    var clickable: Boolean
    var geodesic: Boolean
    var zIndex: Float = 0.00f
    var tag: String = ""
    var googleMapsPolygon: Polygon? = null

    init {
        if (!fromJSONObject.has("paths")) {
            throw InvalidArgumentsError("Polygon object is missing the required 'paths' property")
        }

        val pathsArray = fromJSONObject.getJSONArray("paths")
        for (i in 0 until pathsArray.length()) {
            val arr = pathsArray.optJSONArray(i)
            if (arr == null) {
                // is a single shape
                val shape = this.processShape(pathsArray)
                this.shapes.add(shape)
                break
            } else {
                val shape = this.processShape(arr)
                this.shapes.add(shape)
            }
        }

        val strokeOpacity = fromJSONObject.optDouble("strokeOpacity", 1.0)
        strokeColor = this.processColor(fromJSONObject.getString("strokeColor"), strokeOpacity)

        val fillOpacity = fromJSONObject.optDouble("fillOpacity", 1.0)
        fillColor = this.processColor(fromJSONObject.getString("fillColor"), fillOpacity)

        strokeWidth = fromJSONObject.optDouble("strokeWeight", 1.0).toFloat()
        clickable = fromJSONObject.optBoolean("clickable", false)
        geodesic = fromJSONObject.optBoolean("geodesic", false)
        zIndex = fromJSONObject.optDouble("zIndex", 1.0).toFloat()
        tag = fromJSONObject.optString("tag", "")
    }

    private fun processShape(shapeArr: JSONArray): MutableList<LatLng> {
        var shape = mutableListOf<LatLng>()

        for (i in 0 until shapeArr.length()) {
            val obj = shapeArr.getJSONObject(i)
            if (!obj.has("lat") || !obj.has("lng")) {
                throw InvalidArgumentsError("LatLng object is missing the required 'lat' and/or 'lng' property")
            }

            val lat = obj.getDouble("lat")
            val lng = obj.getDouble("lng")

            shape.add(LatLng(lat, lng))
        }

        return shape
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