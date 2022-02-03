package com.capacitorjs.plugins.googlemaps

import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.Marker as GoogleMapMarker
import com.google.android.libraries.maps.model.MarkerOptions

class GoogleMap(val config: GoogleMapConfig, var mapView: MapView? = null) {
    val googleMap: GoogleMap? = null
    val markers = HashMap<String, GoogleMapMarker>()

    fun addMarker(marker: Marker): String {
        googleMap ?: throw Exception("map is not set...")

        // TODO: Run on ui thread
        val markerOptions = MarkerOptions()
        markerOptions.position(marker.coordinate)
        markerOptions.title(marker.title)
        markerOptions.snippet(marker.snippet)
        markerOptions.alpha(marker.opacity)
        markerOptions.flat(marker.isFlat)
        markerOptions.draggable(marker.draggable)

        val newMarker = googleMap.addMarker(markerOptions)


        markers[newMarker.id] = newMarker
        return newMarker.id
    }

    fun removeMarker(id: String) {
        googleMap ?: throw Exception("map is not set...")

        // TODO: Run on ui thread
        val marker = markers[id]
        marker?.let {
            marker.remove()
            markers.remove(marker.id)
        }
    }
}
