package com.capacitorjs.plugins.googlemaps

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.getcapacitor.Bridge
import com.getcapacitor.JSObject
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class CapacitorGoogleMap(
        val id: String,
        val config: GoogleMapConfig,
        val delegate: CapacitorGoogleMapsPlugin
) : OnMapReadyCallback, OnMapClickListener, OnMarkerClickListener {
    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null
    private val markers = HashMap<String, CapacitorGoogleMapMarker>()
    private var clusterManager: ClusterManager<CapacitorGoogleMapMarker>? = null

    private val isReadyChannel = Channel<Boolean>()

    init {
        initMap()
        setListeners()
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
                    it.googleMapMarker = googleMapMarker

                    if (clusterManager != null) {
                        googleMapMarker?.remove()
                    }

                    markers[googleMapMarker!!.id] = it
                    markerIds.add(googleMapMarker.id)
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

        var markerId: String

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

    @SuppressLint("PotentialBehaviorOverride")
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

                clusterManager?.setOnClusterItemClickListener {
                    if (null == it.googleMapMarker) false
                    else this@CapacitorGoogleMap.onMarkerClick(it.googleMapMarker!!)
                }

                googleMap?.setOnMarkerClickListener(clusterManager)

                // add existing markers to the cluster
                if (markers.isNotEmpty()) {
                    for ((_, marker) in markers) {
                        marker.googleMapMarker?.remove()
                        // marker.googleMapMarker = null
                    }
                    clusterManager?.addItems(markers.values)
                    clusterManager?.cluster()
                }
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    fun disableClustering() {
        googleMap ?: throw GoogleMapsError("google map is not available")

        runBlocking {
            withContext(Dispatchers.Main) {
                clusterManager?.clearItems()
                clusterManager?.cluster()
                clusterManager = null

                googleMap?.setOnMarkerClickListener(this@CapacitorGoogleMap)

                // add existing markers back to the map
                if (markers.isNotEmpty()) {
                    for ((_, marker) in markers) {
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
                if (clusterManager != null) {
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

    private fun getScaledPixels(bridge: Bridge, pixels: Int): Int {
        // Get the screen's density scale
        val scale = bridge.activity.resources.displayMetrics.density
        // Convert the dps to pixels, based on density scale
        return (pixels * scale + 0.5f).toInt()
    }

    fun onStart() {
        mapView?.onStart()
    }

    fun onResume() {
        mapView?.onResume()
    }

    fun onStop() {
        mapView?.onStop()
    }

    fun onPause() {
        mapView?.onPause()
    }

    fun onDestroy() {
        mapView?.onDestroy()
    }

    override fun onMapReady(map: GoogleMap) {
        runBlocking {
            googleMap = map

            val data = JSObject()
            data.put("id", this@CapacitorGoogleMap.id)
            delegate.notify("onMapReady", data)

            isReadyChannel.send(true)
            isReadyChannel.close()
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    fun setListeners() {
        runBlocking {
            withContext(Dispatchers.Main) {
                this@CapacitorGoogleMap.googleMap?.setOnMarkerClickListener(this@CapacitorGoogleMap)
                this@CapacitorGoogleMap.googleMap?.setOnMapClickListener(this@CapacitorGoogleMap)
            }
        }
    }

    override fun onMapClick(point: LatLng) {
        val data = JSObject()
        data.put("id", this@CapacitorGoogleMap.id)
        data.put("latitude", point.latitude)
        data.put("longitude", point.longitude)
        delegate.notify("onMapClick", data)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val data = JSObject()
        data.put("mapId", this@CapacitorGoogleMap.id)
        data.put("markerId", marker.id)
        data.put("latitude", marker.position.latitude)
        data.put("longitude", marker.position.longitude)
        data.put("title", marker.title)
        data.put("snippet", marker.snippet)
        delegate.notify("onMarkerClick", data)
        return false
    }
}
