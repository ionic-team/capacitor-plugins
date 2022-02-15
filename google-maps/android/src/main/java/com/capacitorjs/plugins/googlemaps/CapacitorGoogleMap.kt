package com.capacitorjs.plugins.googlemaps

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.getcapacitor.Bridge
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*

import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.channels.Channel

class CapacitorGoogleMap(val id: String, val config: GoogleMapConfig,
                         val delegate: CapacitorGoogleMapsPlugin) : OnMapReadyCallback {
    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null
    private val markers = HashMap<String, CapacitorGoogleMapMarker>()
    private var clusterManager: ClusterManager<CapacitorGoogleMapMarker>? = null

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
                this@CapacitorGoogleMap.clusterManager = null
            }
        }
    }

    fun addMarker(marker: CapacitorGoogleMapMarker): String {
        googleMap ?: throw GoogleMapsError("google map is not available")

        var markerId = ""

        runBlocking {
            withContext(Dispatchers.Main) {
                val googleMapMarker = googleMap!!.addMarker(marker.getMarkerOptions())

                if (this@CapacitorGoogleMap.clusterManager != null) {
                    marker.googleMapMarker?.remove()
                    marker.googleMapMarker = null
                    this@CapacitorGoogleMap.clusterManager?.addItem(marker)
                } else {
                    marker.googleMapMarker = googleMapMarker
                }

                markers[googleMapMarker!!.id] = marker

                markerId = googleMapMarker.id
            }
        }

        return markerId
    }

    fun enableClustering() {
        googleMap ?: throw GoogleMapsError("google map is not available")

        if (clusterManager != null) {
            return
        }

        runBlocking {
            withContext(Dispatchers.Main) {
                val bridge = delegate.bridge
                clusterManager = ClusterManager(bridge.context, googleMap)

                googleMap?.setOnCameraIdleListener(clusterManager)
                googleMap?.setOnMarkerClickListener(clusterManager)

                // add existing markers to the cluster
                if (markers.isNotEmpty()) {
                    for ((id, marker) in this@CapacitorGoogleMap.markers) {
                        marker.googleMapMarker?.remove()
                        marker.googleMapMarker = null
                    }
                    clusterManager?.addItems(this@CapacitorGoogleMap.markers.values)
                    clusterManager?.cluster()
                }
            }
        }
    }

    fun disableClustering() {
        googleMap ?: throw GoogleMapsError("google map is not available")

        runBlocking {
            withContext(Dispatchers.Main) {
                clusterManager?.clearItems()
                clusterManager = null

                // add existing markers back to the map
                if (markers.isNotEmpty()) {
                    for ((id, marker) in this@CapacitorGoogleMap.markers) {
                        val googleMapMarker = googleMap?.addMarker(marker.getMarkerOptions())
                        marker.googleMapMarker = googleMapMarker
                    }
                }
            }
        }


    }

    fun removeMarker(id: String) {
        googleMap ?: throw GoogleMapsError("google map is not available")

        val marker = markers[id]
        marker ?: throw MarkerNotFoundError()

        runBlocking {
            withContext(Dispatchers.Main) {
                if(this@CapacitorGoogleMap.clusterManager != null) {
                    this@CapacitorGoogleMap.clusterManager?.removeItem(marker)
                }

                marker.googleMapMarker?.remove()
                markers.remove(id)
            }
        }
    }

    private fun getScaledPixels(bridge: Bridge, pixels: Int): Int {
        // Get the screen's density scale
        val scale = bridge.activity.resources.displayMetrics.density
        // Convert the dps to pixels, based on density scale
        return (pixels * scale + 0.5f).toInt()
    }


    override fun onMapReady(map: GoogleMap) {
        runBlocking {
            googleMap = map
            this@CapacitorGoogleMap.isReadyChannel.send(true);
            this@CapacitorGoogleMap.isReadyChannel.close()
        }
    }
}
