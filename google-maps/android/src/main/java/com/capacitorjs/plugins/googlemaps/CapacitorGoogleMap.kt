package com.capacitorjs.plugins.googlemaps

import android.annotation.SuppressLint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import com.getcapacitor.Bridge
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
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
            CoroutineScope(Dispatchers.Main).launch {
                val bridge = delegate.bridge

                val mapView = MapView(bridge.context, config.googleMapOptions)
                mapView.onCreate(null)
                mapView.onStart()
                mapView.getMapAsync(this@CapacitorGoogleMap)

                isReadyChannel.receive()

                this@CapacitorGoogleMap.mapView = mapView

                render()
            }
        }
    }

    private fun render() {
        this.mapView ?: throw GoogleMapsError("map view is not available")

        runBlocking {
            CoroutineScope(Dispatchers.Main).launch {
                val bridge = delegate.bridge

                val mapViewParent = FrameLayout(bridge.context)
                val layoutParams =
                    FrameLayout.LayoutParams(
                        getScaledPixels(bridge, config.width),
                        getScaledPixels(bridge, config.height),
                    )
                layoutParams.leftMargin = getScaledPixels(bridge, config.x)
                layoutParams.topMargin = getScaledPixels(bridge, config.y)

                mapViewParent.tag = id

                mapView!!.layoutParams = layoutParams
                mapViewParent.addView(mapView)

                ((bridge.webView.parent) as ViewGroup).addView(mapViewParent)
            }
        }
    }

    fun updateRender(updatedBounds: CapacitorGoogleMapsBounds) {
        this.mapView ?: throw GoogleMapsError("map view is not available")

        this.config.x = updatedBounds.x
        this.config.y = updatedBounds.y
        this.config.width = updatedBounds.width
        this.config.height = updatedBounds.height

        runBlocking {
            CoroutineScope(Dispatchers.Main).launch {
                val bridge = delegate.bridge

                val viewToUpdate: View? = ((bridge.webView.parent) as FrameLayout).findViewWithTag(id)
                if (null != viewToUpdate) {
                    val layoutParams =
                        FrameLayout.LayoutParams(
                            getScaledPixels(bridge, config.width),
                            getScaledPixels(bridge, config.height),
                        )
                    layoutParams.leftMargin = getScaledPixels(bridge, config.x)
                    layoutParams.topMargin = getScaledPixels(bridge, config.y)
                    try {
                        viewToUpdate.layoutParams = layoutParams
                    } catch(e: Exception) {
                        Log.d("GOOGLE_MAPS", e.localizedMessage)
                    }
                }

            }
        }
    }

    fun destroy() {
        runBlocking {
            CoroutineScope(Dispatchers.Main).launch {
                val bridge = delegate.bridge

                val viewToRemove: View? = ((bridge.webView.parent) as ViewGroup).findViewWithTag(id)
                if (null != viewToRemove) {
                    ((bridge.webView.parent) as ViewGroup).removeView(viewToRemove)
                }
                mapView?.onDestroy()
                mapView = null
                googleMap = null
                clusterManager = null
            }
        }
    }

    fun addMarkers(newMarkers: List<CapacitorGoogleMapMarker>, callback: (ids: Result<List<String>>) -> Unit) {
        try {
            googleMap ?: throw GoogleMapsError("google map is not available")
            val markerIds: MutableList<String> = mutableListOf()

            CoroutineScope(Dispatchers.Main).launch {
                newMarkers.forEach {
                    val googleMapMarker = googleMap?.addMarker(it.getMarkerOptions())

                    if (clusterManager == null) {
                        it.googleMapMarker = googleMapMarker
                    } else {
                        googleMapMarker?.remove()
                    }

                    markers[googleMapMarker!!.id] = it
                    markerIds.add(googleMapMarker!!.id)
                }

                if (clusterManager != null) {
                    clusterManager?.addItems(newMarkers)
                    clusterManager?.cluster()
                }

                callback(Result.success(markerIds))
            }
        } catch(e: GoogleMapsError) {
            callback(Result.failure(e))
        }
    }

    fun addMarker(marker: CapacitorGoogleMapMarker, callback: (result: Result<String>) -> Unit) {
        try {
            googleMap ?: throw GoogleMapsError("google map is not available")

            var markerId = ""

            CoroutineScope(Dispatchers.Main).launch {
                val googleMapMarker = googleMap?.addMarker(marker.getMarkerOptions())

                if (clusterManager == null) {
                    marker.googleMapMarker = googleMapMarker
                } else {
                    googleMapMarker?.remove()
                    clusterManager?.addItem(marker)
                    clusterManager?.cluster()
                }

                markers[googleMapMarker!!.id] = marker

                markerId = googleMapMarker.id

                callback(Result.success(markerId))
            }
        } catch(e: GoogleMapsError) {
            callback(Result.failure(e))
        }
    }

    fun enableClustering(callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapsError("google map is not available")

            if (clusterManager != null) {
                callback(null)
                return
            }

            CoroutineScope(Dispatchers.Main).launch {
                val bridge = delegate.bridge
                clusterManager = ClusterManager(bridge.context, googleMap)

                googleMap?.setOnCameraIdleListener(clusterManager)
                googleMap?.setOnMarkerClickListener(clusterManager)

                // add existing markers to the cluster
                if (markers.isNotEmpty()) {
                    for ((id, marker) in markers) {
                        marker.googleMapMarker?.remove()
                        marker.googleMapMarker = null
                    }
                    clusterManager?.addItems(markers.values)
                    clusterManager?.cluster()
                }

                callback(null)
            }
        } catch(e: GoogleMapsError) {
            callback(e)
        }
    }

    fun disableClustering(callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapsError("google map is not available")

            CoroutineScope(Dispatchers.Main).launch {
                clusterManager?.clearItems()
                clusterManager?.cluster()
                clusterManager = null

                // add existing markers back to the map
                if (markers.isNotEmpty()) {
                    for ((id, marker) in markers) {
                        val googleMapMarker = googleMap?.addMarker(marker.getMarkerOptions())
                        marker.googleMapMarker = googleMapMarker
                    }
                }

                callback(null)
            }
        } catch (e: GoogleMapsError) {
            callback(e)
        }
    }

    fun removeMarker(id: String, callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapsError("google map is not available")

            val marker = markers[id]
            marker ?: throw MarkerNotFoundError()

            CoroutineScope(Dispatchers.Main).launch {
                if(clusterManager != null) {
                    clusterManager?.removeItem(marker)
                    clusterManager?.cluster()
                }

                marker.googleMapMarker?.remove()
                markers.remove(id)

                callback(null)
            }
        } catch (e: GoogleMapsError) {
            callback(e)
        }
    }

    fun removeMarkers(ids: List<String>, callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapsError("google map is not available")

            CoroutineScope(Dispatchers.Main).launch {
                val deletedMarkers: MutableList<CapacitorGoogleMapMarker> = mutableListOf()

                ids.forEach {
                    val marker = markers[it]
                    if (marker != null) {
                        marker.googleMapMarker?.remove()
                        markers.remove(it)

                        deletedMarkers.add(marker)
                    }
                }

                if (clusterManager != null) {
                    clusterManager?.removeItems(deletedMarkers)
                    clusterManager?.cluster()
                }

                callback(null)
            }
        } catch(e: GoogleMapsError) {
            callback(e)
        }
    }

    fun setCamera(config: GoogleMapCameraConfig, callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapsError("google map is not available")
            CoroutineScope(Dispatchers.Main).launch {
                val currentPosition = googleMap!!.cameraPosition

                var updatedTarget = config.coordinate
                if (updatedTarget == null) {
                    updatedTarget = currentPosition.target
                }

                var zoom = config.zoom
                if (zoom == null) {
                    zoom = currentPosition.zoom.toDouble()
                }

                var bearing = config.bearing
                if (bearing == null) {
                    bearing = currentPosition.bearing.toDouble()
                }

                var angle = config.angle
                if (angle == null) {
                    angle = currentPosition.tilt.toDouble()
                }

                var animate = config.animate
                if (animate == null) {
                    animate = false
                }

                val updatedPosition = CameraPosition.Builder()
                    .target(updatedTarget)
                    .zoom(zoom.toFloat())
                    .bearing(bearing.toFloat())
                    .tilt(angle.toFloat())
                    .build()

                if (animate) {
                    googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(updatedPosition))
                } else {
                    googleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(updatedPosition))
                }
                callback(null)
            }
        } catch(e: GoogleMapsError) {
            callback(e)
        }
    }


    fun setMapType(mapType: String, callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapsError("google map is not available")
            CoroutineScope(Dispatchers.Main).launch {
                val mapTypeInt: Int = when(mapType) {
                    "Normal" -> GoogleMap.MAP_TYPE_NORMAL
                    "Hybrid" -> GoogleMap.MAP_TYPE_HYBRID
                    "Satellite" -> GoogleMap.MAP_TYPE_SATELLITE
                    "Terrain" -> GoogleMap.MAP_TYPE_TERRAIN
                    "None" -> GoogleMap.MAP_TYPE_NONE
                    else -> {
                        Log.w("CapacitorGoogleMaps", "unknown mapView type '$mapType'  Defaulting to normal.")
                        GoogleMap.MAP_TYPE_NORMAL
                    }
                }

                googleMap!!.mapType = mapTypeInt
                callback(null)
            }
        } catch (e: GoogleMapsError) {
            callback(e)
        }
    }

    fun enableIndoorMaps(enabled: Boolean, callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapsError("google map is not available")
            CoroutineScope(Dispatchers.Main).launch {
                googleMap!!.isIndoorEnabled = enabled
                callback(null)
            }
        } catch (e: GoogleMapsError) {
            callback(e)
        }
    }

    fun enableTrafficLayer(enabled: Boolean, callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapsError("google map is not available")
            CoroutineScope(Dispatchers.Main).launch {
                googleMap!!.isTrafficEnabled = enabled
                callback(null)
            }
        } catch (e: GoogleMapsError) {
            callback(e)
        }
    }

    @SuppressLint("MissingPermission")
    fun enableCurrentLocation(enabled: Boolean, callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapsError("google map is not available")
            CoroutineScope(Dispatchers.Main).launch {
                googleMap!!.isMyLocationEnabled = enabled
                callback(null)
            }
        } catch(e: GoogleMapsError) {
           callback(e)
        }
    }

    fun setPadding(padding: GoogleMapPadding, callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapsError("google map is not available")
            CoroutineScope(Dispatchers.Main).launch {
                googleMap!!.setPadding(padding.left, padding.top, padding.right, padding.bottom)
                callback(null)
            }
        } catch(e: GoogleMapsError) {
            callback(e)
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
            isReadyChannel.send(true);
            isReadyChannel.close()
        }
    }
}
