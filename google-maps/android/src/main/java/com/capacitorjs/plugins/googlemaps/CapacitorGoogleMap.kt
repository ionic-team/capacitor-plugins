package com.capacitorjs.plugins.googlemaps

import android.annotation.SuppressLint
import android.graphics.*
import android.location.Location
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.getcapacitor.Bridge
import com.getcapacitor.JSArray
import com.getcapacitor.JSObject
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CapacitorGoogleMap(
        val id: String,
        val config: GoogleMapConfig,
        val delegate: CapacitorGoogleMapsPlugin
) :
        OnCameraIdleListener,
        OnCameraMoveStartedListener,
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        OnMapClickListener,
        OnMarkerClickListener,
        OnMarkerDragListener,
        OnInfoWindowClickListener {
    private var mapView: MapView
    private var googleMap: GoogleMap? = null
    private val markers = HashMap<String, CapacitorGoogleMapMarker>()
    private val markerIcons = HashMap<String, BitmapDescriptor>()
    private var clusterManager: ClusterManager<CapacitorGoogleMapMarker>? = null

    private val isReadyChannel = Channel<Boolean>()

    init {
        val bridge = delegate.bridge
        mapView = MapView(bridge.context, config.googleMapOptions)
        initMap()
        setListeners()
    }

    private fun initMap() {
        runBlocking {
            val job =
                    CoroutineScope(Dispatchers.Main).launch {
                        mapView.onCreate(null)
                        mapView.onStart()
                        mapView.getMapAsync(this@CapacitorGoogleMap)
                        mapView.setWillNotDraw(false)

                        isReadyChannel.receive()

                        render()
                    }

            job.join()
        }
    }

    private fun render() {
        runBlocking {
            CoroutineScope(Dispatchers.Main).launch {
                val bridge = delegate.bridge
                val mapViewParent = FrameLayout(bridge.context)
                mapViewParent.minimumHeight = bridge.webView.height
                mapViewParent.minimumWidth = bridge.webView.width

                val layoutParams =
                        FrameLayout.LayoutParams(
                                getScaledPixels(bridge, config.width),
                                getScaledPixels(bridge, config.height),
                        )
                layoutParams.leftMargin = getScaledPixels(bridge, config.x)
                layoutParams.topMargin = getScaledPixels(bridge, config.y)

                mapViewParent.tag = id

                mapView.layoutParams = layoutParams
                mapViewParent.addView(mapView)

                ((bridge.webView.parent) as ViewGroup).addView(mapViewParent)

                bridge.webView.bringToFront()
                bridge.webView.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    fun updateRender(updatedBounds: RectF) {
        this.config.x = updatedBounds.left.toInt()
        this.config.y = updatedBounds.top.toInt()
        this.config.width = updatedBounds.width().toInt()
        this.config.height = updatedBounds.height().toInt()

        runBlocking {
            CoroutineScope(Dispatchers.Main).launch {
                val mapRect = getScaledRect(delegate.bridge, updatedBounds)
                this@CapacitorGoogleMap.mapView.x = mapRect.left
                this@CapacitorGoogleMap.mapView.y = mapRect.top
            }
        }
    }

    fun dispatchTouchEvent(event: MotionEvent) {
        CoroutineScope(Dispatchers.Main).launch {
            val offsetViewBounds = getMapBounds()

            val relativeTop = offsetViewBounds.top
            val relativeLeft = offsetViewBounds.left

            event.setLocation(event.x - relativeLeft, event.y - relativeTop)
            mapView.dispatchTouchEvent(event)
        }
    }

    fun bringToFront() {
        CoroutineScope(Dispatchers.Main).launch {
            val mapViewParent =
                    ((delegate.bridge.webView.parent) as ViewGroup).findViewWithTag<ViewGroup>(
                            this@CapacitorGoogleMap.id
                    )
            mapViewParent.bringToFront()
        }
    }

    fun destroy() {
        runBlocking {
            val job =
                    CoroutineScope(Dispatchers.Main).launch {
                        val bridge = delegate.bridge

                        val viewToRemove: View? =
                                ((bridge.webView.parent) as ViewGroup).findViewWithTag(id)
                        if (null != viewToRemove) {
                            ((bridge.webView.parent) as ViewGroup).removeView(viewToRemove)
                        }
                        mapView.onDestroy()
                        googleMap = null
                        clusterManager = null
                    }

            job.join()
        }
    }

    fun addMarkers(
            newMarkers: List<CapacitorGoogleMapMarker>,
            callback: (ids: Result<List<String>>) -> Unit
    ) {
        try {
            googleMap ?: throw GoogleMapNotAvailable()
            val markerIds: MutableList<String> = mutableListOf()

            CoroutineScope(Dispatchers.Main).launch {
                newMarkers.forEach {
                    val markerOptions = this@CapacitorGoogleMap.buildMarker(it)
                    val googleMapMarker = googleMap?.addMarker(markerOptions)
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

                callback(Result.success(markerIds))
            }
        } catch (e: GoogleMapsError) {
            callback(Result.failure(e))
        }
    }

    fun addMarker(marker: CapacitorGoogleMapMarker, callback: (result: Result<String>) -> Unit) {
        try {
            googleMap ?: throw GoogleMapNotAvailable()

            var markerId: String

            CoroutineScope(Dispatchers.Main).launch {
                val markerOptions = this@CapacitorGoogleMap.buildMarker(marker)
                val googleMapMarker = googleMap?.addMarker(markerOptions)

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
        } catch (e: GoogleMapsError) {
            callback(Result.failure(e))
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    fun enableClustering(callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapNotAvailable()

            if (clusterManager != null) {
                callback(null)
                return
            }

            CoroutineScope(Dispatchers.Main).launch {
                val bridge = delegate.bridge
                clusterManager = ClusterManager(bridge.context, googleMap)

                googleMap?.setOnCameraIdleListener(clusterManager)

                setClusterListeners()

                googleMap?.setOnMarkerClickListener(clusterManager)
                googleMap?.setOnInfoWindowClickListener(clusterManager)

                // add existing markers to the cluster
                if (markers.isNotEmpty()) {
                    for ((_, marker) in markers) {
                        marker.googleMapMarker?.remove()
                        // marker.googleMapMarker = null
                    }
                    clusterManager?.addItems(markers.values)
                    clusterManager?.cluster()
                }

                callback(null)
            }
        } catch (e: GoogleMapsError) {
            callback(e)
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    fun disableClustering(callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapNotAvailable()

            CoroutineScope(Dispatchers.Main).launch {
                clusterManager?.clearItems()
                clusterManager?.cluster()
                clusterManager = null

                googleMap?.setOnMarkerClickListener(this@CapacitorGoogleMap)

                // add existing markers back to the map
                if (markers.isNotEmpty()) {
                    for ((_, marker) in markers) {
                        val markerOptions = this@CapacitorGoogleMap.buildMarker(marker)
                        val googleMapMarker = googleMap?.addMarker(markerOptions)
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
            googleMap ?: throw GoogleMapNotAvailable()

            val marker = markers[id]
            marker ?: throw MarkerNotFoundError()

            CoroutineScope(Dispatchers.Main).launch {
                if (clusterManager != null) {
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
            googleMap ?: throw GoogleMapNotAvailable()

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
        } catch (e: GoogleMapsError) {
            callback(e)
        }
    }

    fun setCamera(config: GoogleMapCameraConfig, callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapNotAvailable()
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

                val updatedPosition =
                        CameraPosition.Builder()
                                .target(updatedTarget)
                                .zoom(zoom.toFloat())
                                .bearing(bearing.toFloat())
                                .tilt(angle.toFloat())
                                .build()

                if (animate) {
                    googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(updatedPosition))
                } else {
                    googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(updatedPosition))
                }
                callback(null)
            }
        } catch (e: GoogleMapsError) {
            callback(e)
        }
    }

    fun setMapType(mapType: String, callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapNotAvailable()
            CoroutineScope(Dispatchers.Main).launch {
                val mapTypeInt: Int =
                        when (mapType) {
                            "Normal" -> MAP_TYPE_NORMAL
                            "Hybrid" -> MAP_TYPE_HYBRID
                            "Satellite" -> MAP_TYPE_SATELLITE
                            "Terrain" -> MAP_TYPE_TERRAIN
                            "None" -> MAP_TYPE_NONE
                            else -> {
                                Log.w(
                                        "CapacitorGoogleMaps",
                                        "unknown mapView type '$mapType'  Defaulting to normal."
                                )
                                MAP_TYPE_NORMAL
                            }
                        }

                googleMap?.mapType = mapTypeInt
                callback(null)
            }
        } catch (e: GoogleMapsError) {
            callback(e)
        }
    }

    fun enableIndoorMaps(enabled: Boolean, callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapNotAvailable()
            CoroutineScope(Dispatchers.Main).launch {
                googleMap?.isIndoorEnabled = enabled
                callback(null)
            }
        } catch (e: GoogleMapsError) {
            callback(e)
        }
    }

    fun enableTrafficLayer(enabled: Boolean, callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapNotAvailable()
            CoroutineScope(Dispatchers.Main).launch {
                googleMap?.isTrafficEnabled = enabled
                callback(null)
            }
        } catch (e: GoogleMapsError) {
            callback(e)
        }
    }

    @SuppressLint("MissingPermission")
    fun enableCurrentLocation(enabled: Boolean, callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapNotAvailable()
            CoroutineScope(Dispatchers.Main).launch {
                googleMap?.isMyLocationEnabled = enabled
                callback(null)
            }
        } catch (e: GoogleMapsError) {
            callback(e)
        }
    }

    fun setPadding(padding: GoogleMapPadding, callback: (error: GoogleMapsError?) -> Unit) {
        try {
            googleMap ?: throw GoogleMapNotAvailable()
            CoroutineScope(Dispatchers.Main).launch {
                googleMap?.setPadding(padding.left, padding.top, padding.right, padding.bottom)
                callback(null)
            }
        } catch (e: GoogleMapsError) {
            callback(e)
        }
    }

    fun getMapBounds(): Rect {
        return Rect(
                getScaledPixels(delegate.bridge, config.x),
                getScaledPixels(delegate.bridge, config.y),
                getScaledPixels(delegate.bridge, config.x + config.width),
                getScaledPixels(delegate.bridge, config.y + config.height)
        )
    }

    fun getLatLngBounds(): LatLngBounds {
        return googleMap?.projection?.visibleRegion?.latLngBounds
            ?: throw BoundsNotFoundError()
    }

    fun getLatLngBoundsJSObject(bounds: LatLngBounds): JSObject {
        val data = JSObject()

        val southwestJS = JSObject()
        val centerJS = JSObject()
        val northeastJS = JSObject()

        southwestJS.put("lat", bounds.southwest.latitude)
        southwestJS.put("lng", bounds.southwest.longitude)
        centerJS.put("lat", bounds.center.latitude)
        centerJS.put("lng", bounds.center.longitude)
        northeastJS.put("lat", bounds.northeast.latitude)
        northeastJS.put("lng", bounds.northeast.longitude)

        data.put("southwest", southwestJS)
        data.put("center", centerJS)
        data.put("northeast", northeastJS)

        return data
    }

    private fun getScaledPixels(bridge: Bridge, pixels: Int): Int {
        // Get the screen's density scale
        val scale = bridge.activity.resources.displayMetrics.density
        // Convert the dps to pixels, based on density scale
        return (pixels * scale + 0.5f).toInt()
    }

    private fun getScaledPixelsF(bridge: Bridge, pixels: Float): Float {
        // Get the screen's density scale
        val scale = bridge.activity.resources.displayMetrics.density
        // Convert the dps to pixels, based on density scale
        return (pixels * scale + 0.5f)
    }

    private fun getScaledRect(bridge: Bridge, rectF: RectF): RectF {
        return RectF(
                getScaledPixelsF(bridge, rectF.left),
                getScaledPixelsF(bridge, rectF.top),
                getScaledPixelsF(bridge, rectF.right),
                getScaledPixelsF(bridge, rectF.bottom)
        )
    }

    private fun buildMarker(marker: CapacitorGoogleMapMarker): MarkerOptions {
        val markerOptions = MarkerOptions()
        markerOptions.position(marker.coordinate)
        markerOptions.title(marker.title)
        markerOptions.snippet(marker.snippet)
        markerOptions.alpha(marker.opacity)
        markerOptions.flat(marker.isFlat)
        markerOptions.draggable(marker.draggable)

        if (!marker.iconUrl.isNullOrEmpty()) {
            if (this.markerIcons.contains(marker.iconUrl)) {
                val cachedIcon = this.markerIcons[marker.iconUrl]
                markerOptions.icon(cachedIcon)
            } else {
                try {
                    val stream = this.delegate.context.assets.open("public/${marker.iconUrl}")
                    var bitmap = BitmapFactory.decodeStream(stream)

                    if (marker.iconSize != null) {
                        bitmap = Bitmap.createScaledBitmap(bitmap, (marker.iconSize!!.width * this.config.devicePixelRatio).toInt(), (marker.iconSize!!.height * this.config.devicePixelRatio).toInt(), false)
                    }

                    val icon = BitmapDescriptorFactory.fromBitmap(bitmap)
                    this.markerIcons[marker.iconUrl!!] = icon
                    markerOptions.icon(icon)
                } catch(e: Exception) {
                    var detailedMessage = "${e.javaClass} - ${e.localizedMessage}"
                    if (marker.iconUrl!!.endsWith(".svg")) {
                        detailedMessage = "SVG not supported"
                    }

                    Log.w("CapacitorGoogleMaps", "Could not load image '${marker.iconUrl}': ${detailedMessage}. Using default marker icon.")
                }
            }
        } else {
            if (marker.colorHue != null) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(marker.colorHue!!))
            }
        }

        return markerOptions
    }

    fun onStart() {
        mapView.onStart()
    }

    fun onResume() {
        mapView.onResume()
    }

    fun onStop() {
        mapView.onStop()
    }

    fun onPause() {
        mapView.onPause()
    }

    fun onDestroy() {
        mapView.onDestroy()
    }

    override fun onMapReady(map: GoogleMap) {
        runBlocking {
            googleMap = map

            val data = JSObject()
            data.put("mapId", this@CapacitorGoogleMap.id)
            delegate.notify("onMapReady", data)

            isReadyChannel.send(true)
            isReadyChannel.close()
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    fun setListeners() {
        CoroutineScope(Dispatchers.Main).launch {
            this@CapacitorGoogleMap.googleMap?.setOnCameraIdleListener(this@CapacitorGoogleMap)
            this@CapacitorGoogleMap.googleMap?.setOnCameraMoveStartedListener(
                    this@CapacitorGoogleMap
            )
            this@CapacitorGoogleMap.googleMap?.setOnMarkerClickListener(this@CapacitorGoogleMap)
            this@CapacitorGoogleMap.googleMap?.setOnMarkerDragListener(this@CapacitorGoogleMap)
            this@CapacitorGoogleMap.googleMap?.setOnMapClickListener(this@CapacitorGoogleMap)
            this@CapacitorGoogleMap.googleMap?.setOnMyLocationButtonClickListener(
                    this@CapacitorGoogleMap
            )
            this@CapacitorGoogleMap.googleMap?.setOnMyLocationClickListener(this@CapacitorGoogleMap)
            this@CapacitorGoogleMap.googleMap?.setOnInfoWindowClickListener(this@CapacitorGoogleMap)
        }
    }

    fun setClusterListeners() {
        CoroutineScope(Dispatchers.Main).launch {
            clusterManager?.setOnClusterItemClickListener {
                if (null == it.googleMapMarker) false
                else this@CapacitorGoogleMap.onMarkerClick(it.googleMapMarker!!)
            }

            clusterManager?.setOnClusterItemInfoWindowClickListener {
                if (null != it.googleMapMarker) {
                    this@CapacitorGoogleMap.onInfoWindowClick(it.googleMapMarker!!)
                }
            }

            clusterManager?.setOnClusterInfoWindowClickListener {
                val data = this@CapacitorGoogleMap.getClusterData(it)
                delegate.notify("onClusterInfoWindowClick", data)
            }

            clusterManager?.setOnClusterClickListener {
                val data = this@CapacitorGoogleMap.getClusterData(it)
                delegate.notify("onClusterClick", data)
                false
            }
        }
    }

    private fun getClusterData(it: Cluster<CapacitorGoogleMapMarker>): JSObject {
        val data = JSObject()
        data.put("mapId", this.id)
        data.put("latitude", it.position.latitude)
        data.put("longitude", it.position.longitude)
        data.put("size", it.size)

        val items = JSArray()
        for (item in it.items) {
            val marker = item.googleMapMarker

            if (marker != null) {
                val jsItem = JSObject()
                jsItem.put("markerId", marker.id)
                jsItem.put("latitude", marker.position.latitude)
                jsItem.put("longitude", marker.position.longitude)
                jsItem.put("title", marker.title)
                jsItem.put("snippet", marker.snippet)

                items.put(jsItem)
            }
        }

        data.put("items", items)

        return data
    }

    override fun onMapClick(point: LatLng) {
        val data = JSObject()
        data.put("mapId", this@CapacitorGoogleMap.id)
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

    override fun onMarkerDrag(marker: Marker) {
        val data = JSObject()
        data.put("mapId", this@CapacitorGoogleMap.id)
        data.put("markerId", marker.id)
        data.put("latitude", marker.position.latitude)
        data.put("longitude", marker.position.longitude)
        data.put("title", marker.title)
        data.put("snippet", marker.snippet)
        delegate.notify("onMarkerDrag", data)
    }

    override fun onMarkerDragStart(marker: Marker) {
        val data = JSObject()
        data.put("mapId", this@CapacitorGoogleMap.id)
        data.put("markerId", marker.id)
        data.put("latitude", marker.position.latitude)
        data.put("longitude", marker.position.longitude)
        data.put("title", marker.title)
        data.put("snippet", marker.snippet)
        delegate.notify("onMarkerDragStart", data)
    }

    override fun onMarkerDragEnd(marker: Marker) {
        val data = JSObject()
        data.put("mapId", this@CapacitorGoogleMap.id)
        data.put("markerId", marker.id)
        data.put("latitude", marker.position.latitude)
        data.put("longitude", marker.position.longitude)
        data.put("title", marker.title)
        data.put("snippet", marker.snippet)
        delegate.notify("onMarkerDragEnd", data)
    }

    override fun onMyLocationButtonClick(): Boolean {
        val data = JSObject()
        data.put("mapId", this@CapacitorGoogleMap.id)
        delegate.notify("onMyLocationButtonClick", data)
        return false
    }

    override fun onMyLocationClick(location: Location) {
        val data = JSObject()
        data.put("mapId", this@CapacitorGoogleMap.id)
        data.put("latitude", location.latitude)
        data.put("longitude", location.longitude)
        delegate.notify("onMyLocationClick", data)
    }

    override fun onCameraIdle() {
        val data = JSObject()
        data.put("mapId", this@CapacitorGoogleMap.id)
        data.put("bounds", getLatLngBoundsJSObject(getLatLngBounds()))
        data.put("bearing", this@CapacitorGoogleMap.googleMap?.cameraPosition?.bearing)
        data.put("latitude", this@CapacitorGoogleMap.googleMap?.cameraPosition?.target?.latitude)
        data.put("longitude", this@CapacitorGoogleMap.googleMap?.cameraPosition?.target?.longitude)
        data.put("tilt", this@CapacitorGoogleMap.googleMap?.cameraPosition?.tilt)
        data.put("zoom", this@CapacitorGoogleMap.googleMap?.cameraPosition?.zoom)
        delegate.notify("onCameraIdle", data)
        delegate.notify("onBoundsChanged", data)
    }

    override fun onCameraMoveStarted(reason: Int) {
        val data = JSObject()
        data.put("mapId", this@CapacitorGoogleMap.id)
        data.put("isGesture", reason == 1)
        delegate.notify("onCameraMoveStarted", data)
    }

    override fun onInfoWindowClick(marker: Marker) {
        val data = JSObject()
        data.put("mapId", this@CapacitorGoogleMap.id)
        data.put("markerId", marker.id)
        data.put("latitude", marker.position.latitude)
        data.put("longitude", marker.position.longitude)
        data.put("title", marker.title)
        data.put("snippet", marker.snippet)
        delegate.notify("onInfoWindowClick", data)
    }
}
