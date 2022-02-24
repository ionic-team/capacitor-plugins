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
import kotlinx.coroutines.channels.Channel

class CapacitorGoogleMap(val id: String, val config: GoogleMapConfig,
                         val delegate: CapacitorGoogleMapsPlugin) : OnMapReadyCallback {
    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null
    private val markers = HashMap<String, Marker>()

    private val isReadyChannel = Channel<Boolean>()

    init {
        initMap()
    }

    private fun initMap() {
        runBlocking {
            withContext(Dispatchers.Main) {
                val bridge = delegate.bridge

                val mapView = MapView(bridge.context, this@CapacitorGoogleMap.config.googleMapOptions)
                mapView.onCreate(null)
                mapView.onStart()
                mapView.getMapAsync(this@CapacitorGoogleMap)

                isReadyChannel.receive()

                this@CapacitorGoogleMap.mapView = mapView
            }
        }

        render()
    }

    private fun render() {
        this.mapView ?: throw GoogleMapsError("map view is not available")

        runBlocking {
            withContext(Dispatchers.Main) {
                val bridge = delegate.bridge

                val mapViewParent = FrameLayout(bridge.context)
                val layoutParams =
                        FrameLayout.LayoutParams(
                                getScaledPixels(bridge, this@CapacitorGoogleMap.config.width),
                                getScaledPixels(bridge, this@CapacitorGoogleMap.config.height),
                        )
                layoutParams.leftMargin = getScaledPixels(bridge, this@CapacitorGoogleMap.config.x)
                layoutParams.topMargin = getScaledPixels(bridge, this@CapacitorGoogleMap.config.y)

                mapViewParent.tag = this@CapacitorGoogleMap.id
                this@CapacitorGoogleMap.mapView!!.layoutParams = layoutParams
                mapViewParent.addView(mapView)

                ((bridge.webView.parent) as ViewGroup).addView(mapViewParent)
            }
        }
    }

    fun destroy() {
        runBlocking {
            withContext(Dispatchers.Main) {
                val bridge = delegate.bridge

                val viewToRemove: View? = ((bridge.webView.parent) as ViewGroup).findViewWithTag(this@CapacitorGoogleMap.id)
                if (null != viewToRemove) {
                    ((bridge.webView.parent) as ViewGroup).removeView(viewToRemove)
                }
                this@CapacitorGoogleMap.mapView?.onDestroy()
                this@CapacitorGoogleMap.mapView = null
                this@CapacitorGoogleMap.googleMap = null
            }
        }
    }

    fun addMarker(marker: CapacitorGoogleMapMarker): String {
        googleMap ?: throw GoogleMapsError("google map is not available")

        var markerId = ""

        runBlocking {
            withContext(Dispatchers.Main) {
                val markerOptions = MarkerOptions()
                markerOptions.position(marker.coordinate)
                markerOptions.title(marker.title)
                markerOptions.snippet(marker.snippet)
                markerOptions.alpha(marker.opacity)
                markerOptions.flat(marker.isFlat)
                markerOptions.draggable(marker.draggable)

                val newMarker = googleMap!!.addMarker(markerOptions)
                markers[newMarker.id] = newMarker

                markerId = newMarker.id
            }
        }

        return markerId
    }

    fun removeMarker(id: String) {
        googleMap ?: throw GoogleMapsError("google map is not available")

        val marker = markers[id]
        marker ?: throw MarkerNotFoundError()

        runBlocking {
            withContext(Dispatchers.Main) {
                marker.remove()
                markers.remove(marker.id)
            }
        }
    }

    private fun getScaledPixels(bridge: Bridge, pixels: Int): Int {
        // Get the screen's density scale
        val scale = bridge.activity.resources.displayMetrics.density
        // Convert the dps to pixels, based on density scale
        return (pixels * scale + 0.5f).toInt()
    }

    override fun onMapReady(map: GoogleMap?) {
        runBlocking {
            if(map != null) {
                googleMap = map
                this@CapacitorGoogleMap.isReadyChannel.send(true);
                this@CapacitorGoogleMap.isReadyChannel.close()
            } else {
                this@CapacitorGoogleMap.isReadyChannel.send(false);
                this@CapacitorGoogleMap.isReadyChannel.close()
            }
        }
    }
}
