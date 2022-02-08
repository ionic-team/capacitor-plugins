package com.capacitorjs.plugins.googlemaps

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.getcapacitor.Bridge
import kotlinx.coroutines.*
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.model.Marker
import com.google.android.libraries.maps.model.MarkerOptions

class CapacitorGoogleMap(val id: String, val config: GoogleMapConfig,
                         val delegate: CapacitorGoogleMapsPlugin) : OnMapReadyCallback {
    var mapView: MapView? = null
    private var googleMap: GoogleMap? = null
    private val markers = HashMap<String, Marker>()

    init {
        render()
    }

    private fun render() {
        val bridge = delegate.bridge
        bridge.activity.runOnUiThread {
            val mapViewParent = FrameLayout(bridge.context)
            val layoutParams =
                FrameLayout.LayoutParams(
                    getScaledPixels(bridge, this.config.width),
                    getScaledPixels(bridge, this.config.height),
                )
            layoutParams.leftMargin = getScaledPixels(bridge, this.config.x)
            layoutParams.topMargin = getScaledPixels(bridge, this.config.y)

            val mapView = MapView(bridge.context, this.config.googleMapOptions)

            mapViewParent.tag = this.id
            mapView.layoutParams = layoutParams
            mapViewParent.addView(mapView)

            ((bridge.webView.parent) as ViewGroup).addView(mapViewParent)

            this.mapView = mapView

            mapView.onCreate(null)
            mapView.onStart()
            mapView.getMapAsync(this)
        }
    }

    fun destroy() {
        val bridge = delegate.bridge
        bridge.activity.runOnUiThread {
            val viewToRemove: View? = ((bridge.webView.parent) as ViewGroup).findViewWithTag(this.id)
            if (null != viewToRemove) {
                ((bridge.webView.parent) as ViewGroup).removeView(viewToRemove)
            }
            mapView?.onDestroy()
            mapView = null
        }
    }

    fun addMarker(marker: CapacitorGoogleMapMarker): String {
        googleMap ?: throw GoogleMapsError("google map is not available")

        var markerId = ""

        runBlocking {
            markerId = uiAddMarker(marker)
        }
        
        return markerId
    }

    private suspend fun uiAddMarker(markerConfig: CapacitorGoogleMapMarker): String {
        var newMarkerId = ""

        withContext(Dispatchers.Main) {
            val markerOptions = MarkerOptions()
            markerOptions.position(markerConfig.coordinate)
            markerOptions.title(markerConfig.title)
            markerOptions.snippet(markerConfig.snippet)
            markerOptions.alpha(markerConfig.opacity)
            markerOptions.flat(markerConfig.isFlat)
            markerOptions.draggable(markerConfig.draggable)

            val newMarker = googleMap!!.addMarker(markerOptions)
            markers[newMarker.id] = newMarker

            newMarkerId = newMarker.id
        }

        return newMarkerId
    }

    fun removeMarker(id: String) {
        googleMap ?: throw GoogleMapsError("google map is not available")

        val marker = markers[id]
        marker?.let {
            runBlocking {
                uiRemoveMarker(marker)
            }
        }
    }

    private suspend fun uiRemoveMarker(marker: Marker) {
       withContext(Dispatchers.Main) {
           marker.remove()
           markers.remove(marker.id)
       }
    }

    private fun getScaledPixels(bridge: Bridge, pixels: Int): Int {
        // Get the screen's density scale
        val scale = bridge.activity.resources.displayMetrics.density
        // Convert the dps to pixels, based on density scale
        return (pixels * scale + 0.5f).toInt()
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
    }
}
