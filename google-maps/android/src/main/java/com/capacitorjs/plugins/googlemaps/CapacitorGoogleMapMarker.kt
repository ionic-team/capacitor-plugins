package com.capacitorjs.plugins.googlemaps

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.Size
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterItem
import org.json.JSONObject


class CapacitorGoogleMapMarker(fromJSONObject: JSONObject, context: Context , devicePixelRatio: Float): ClusterItem {
    var coordinate: LatLng = LatLng(0.0, 0.0)
    var opacity: Float = 1.0f
    private var title: String
    private var snippet: String
    var isFlat: Boolean = false
    var iconUrl: String? = null
    var iconSize: Size? = null
    var iconAnchor: CapacitorGoogleMapsPoint? = null
    var icon: BitmapDescriptor? = null
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

        icon = this.getScaledIconBitmapDescriptor(context, devicePixelRatio)

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
        markerOptions.icon(icon)
        return markerOptions
    }


    private fun buildIconAnchorPoint(iconAnchor: CapacitorGoogleMapsPoint): CapacitorGoogleMapsPoint? {
        iconSize ?: return null

        val u: Float = iconAnchor.x / iconSize!!.width
        val v: Float = iconAnchor.y / iconSize!!.height

        return CapacitorGoogleMapsPoint(u, v)
    }

    private fun getScaledIconBitmapDescriptor(context: Context, devicePixelRatio: Float): BitmapDescriptor? {
        iconUrl ?: return null

        try {
            val stream = context.assets.open("public/$iconUrl")
            var bitmap = BitmapFactory.decodeStream(stream)

            if (this.iconSize != null) {
                bitmap = Bitmap.createScaledBitmap(bitmap, (this.iconSize!!.width * devicePixelRatio).toInt(), (this.iconSize!!.height * devicePixelRatio).toInt(), false)
            }

            return BitmapDescriptorFactory.fromBitmap(bitmap)
        } catch (e: Exception) {
            Log.w("GoogleMapsMarkers", e.localizedMessage.toString())
            Log.w("GoogleMapsMarkers", e.stackTrace.toString())

            return null
        }
    }
}