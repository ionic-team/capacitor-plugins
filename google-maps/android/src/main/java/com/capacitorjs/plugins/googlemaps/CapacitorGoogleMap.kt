package com.capacitorjs.plugins.googlemaps

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
            withContext(Dispatchers.Main) {
                val bridge = delegate.bridge

                val mapView = MapView(bridge.context, config.googleMapOptions)
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

    fun destroy() {
        runBlocking {
            withContext(Dispatchers.Main) {
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

    fun addMarkers(newMarkers: List<CapacitorGoogleMapMarker>): List<String> {
        googleMap ?: throw GoogleMapsError("google map is not available")

        val markerIds: MutableList<String> = mutableListOf()

        runBlocking {
            withContext(Dispatchers.Main) {
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
            }
        }


        return markerIds
    }

    fun addMarker(marker: CapacitorGoogleMapMarker): String {
        googleMap ?: throw GoogleMapsError("google map is not available")

        var markerId = ""

        runBlocking {
            withContext(Dispatchers.Main) {
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
                    for ((id, marker) in markers) {
                        marker.googleMapMarker?.remove()
                        marker.googleMapMarker = null
                    }
                    clusterManager?.addItems(markers.values)
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
                clusterManager?.cluster()
                clusterManager = null

                // add existing markers back to the map
                if (markers.isNotEmpty()) {
                    for ((id, marker) in markers) {
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
                if(clusterManager != null) {
                    clusterManager?.removeItem(marker)
                    clusterManager?.cluster()
                }

                marker.googleMapMarker?.remove()
                markers.remove(id)
            }
        }
    }

    fun removeMarkers(ids: List<String>) {
        googleMap ?: throw GoogleMapsError("google map is not available")

        runBlocking {
            withContext(Dispatchers.Main) {
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
            }
        }
    }

    fun setCamera(config: GoogleMapCameraConfig) {
        googleMap ?: throw GoogleMapsError("google map is not available")

        runBlocking {
            withContext(Dispatchers.Main) {
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
            }
        }
    }


    fun setMapType(mapType: String) {
        googleMap ?: throw GoogleMapsError("google map is not available")

        runBlocking {
            withContext(Dispatchers.Main) {
                val mapTypeInt: Int = when(mapType) {
                    "Normal" -> GoogleMap.MAP_TYPE_NORMAL
                    "Hybrid" -> GoogleMap.MAP_TYPE_HYBRID
                    "Satellite" -> GoogleMap.MAP_TYPE_SATELLITE
                    "Terrain" -> GoogleMap.MAP_TYPE_TERRAIN
                    "None" -> GoogleMap.MAP_TYPE_NONE
                    else -> {
                        print("CapacitorGoogleMaps Warning: unknown mapView type '$mapType'  Defaulting to normal.")
                        GoogleMap.MAP_TYPE_NORMAL
                    }
                }

                googleMap!!.mapType = mapTypeInt
            }
        }
    }

    fun enableIndoorMaps(enabled: Boolean) {
        googleMap ?: throw GoogleMapsError("google map is not available")

        runBlocking {
            withContext(Dispatchers.Main) {
                googleMap!!.setIndoorEnabled(enabled)
            }
        }
    }

    fun enableTrafficLayer(enabled: Boolean) {
        googleMap ?: throw GoogleMapsError("google map is not available")

        runBlocking {
            withContext(Dispatchers.Main) {
                googleMap!!.isTrafficEnabled = enabled
            }
        }
    }

    fun enableCurrentLocation(enabled: Boolean) {
        googleMap ?: throw GoogleMapsError("google map is not available")
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    googleMap!!.isMyLocationEnabled = enabled
                } catch(e: SecurityException) {
                    throw PermissionDeniedLocation(e.message)
                }
            }
        }
    }

    fun setPadding(padding: GoogleMapPadding) {
        googleMap ?: throw GoogleMapsError("google map is not available")
        runBlocking {
            withContext(Dispatchers.Main) {
                googleMap!!.setPadding(padding.left, padding.top, padding.right, padding.bottom)
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
            isReadyChannel.send(true);
            isReadyChannel.close()
        }
    }
}
