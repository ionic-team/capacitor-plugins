package com.capacitorjs.plugins.googlemaps

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.getcapacitor.*
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.annotation.Permission
import com.getcapacitor.annotation.PermissionCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@CapacitorPlugin(
        name = "CapacitorGoogleMaps",
        permissions =
                [
                        Permission(
                                strings = [Manifest.permission.ACCESS_FINE_LOCATION],
                                alias = CapacitorGoogleMapsPlugin.LOCATION
                        ),
                ],
)
class CapacitorGoogleMapsPlugin : Plugin() {
    private var maps: HashMap<String, CapacitorGoogleMap> = HashMap()
    private var cachedTouchEvents: HashMap<String, MutableList<MotionEvent>> = HashMap()
    private val tag: String = "CAP-GOOGLE-MAPS"

    companion object {
        const val LOCATION = "location"
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun load() {
        super.load()

        this.bridge.webView.setOnTouchListener(
                object : View.OnTouchListener {
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        if (event != null) {
                            if (event.source == -1) {
                                return v?.onTouchEvent(event) ?: true
                            }

                            val touchX = event.x
                            val touchY = event.y

                            for ((id, map) in maps) {
                                val mapRect = map.getMapBounds()
                                if (mapRect.contains(touchX.toInt(), touchY.toInt())) {
                                    if (event.action == MotionEvent.ACTION_DOWN) {
                                        if (cachedTouchEvents[id] == null) {
                                            cachedTouchEvents[id] = mutableListOf<MotionEvent>()
                                        }

                                        cachedTouchEvents[id]?.clear()
                                    }

                                    val motionEvent = MotionEvent.obtain(event)
                                    cachedTouchEvents[id]?.add(motionEvent)

                                    val payload = JSObject()
                                    payload.put("x", touchX / map.config.devicePixelRatio)
                                    payload.put("y", touchY / map.config.devicePixelRatio)
                                    payload.put("mapId", map.id)

                                    notifyListeners("isMapInFocus", payload)
                                    return true
                                }
                            }
                        }

                        return v?.onTouchEvent(event) ?: true
                    }
                }
        )
    }

    override fun handleOnStart() {
        super.handleOnStart()
        maps.forEach { it.value.onStart() }
    }

    override fun handleOnResume() {
        super.handleOnResume()
        maps.forEach { it.value.onResume() }
    }

    override fun handleOnPause() {
        super.handleOnPause()
        maps.forEach { it.value.onPause() }
    }

    override fun handleOnStop() {
        super.handleOnStop()
        maps.forEach { it.value.onStop() }
    }

    override fun handleOnDestroy() {
        super.handleOnDestroy()
        maps.forEach { it.value.onDestroy() }
    }

    @PluginMethod
    fun create(call: PluginCall) {
        try {
            val id = call.getString("id")

            if (null == id || id.isEmpty()) {
                throw InvalidMapIdError()
            }

            val configObject =
                    call.getObject("config")
                            ?: throw InvalidArgumentsError("config object is missing")

            val forceCreate = call.getBoolean("forceCreate", false)!!

            val config = GoogleMapConfig(configObject)

            if (maps.contains(id)) {
                if (!forceCreate) {
                    call.resolve()
                    return
                }

                val oldMap = maps.remove(id)
                oldMap?.destroy()
            }

            val newMap = CapacitorGoogleMap(id, config, this)
            maps[id] = newMap

            call.resolve()
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun destroy(call: PluginCall) {
        try {
            val id = call.getString("id")

            if (null == id || id.isEmpty()) {
                throw InvalidMapIdError()
            }

            val removedMap = maps.remove(id) ?: throw MapNotFoundError()
            removedMap.destroy()

            call.resolve()
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun addMarker(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val markerObj = call.getObject("marker", null)
            markerObj ?: throw InvalidArgumentsError("marker object is missing")

            val map = maps[id]
            map ?: throw MapNotFoundError()

            val marker = CapacitorGoogleMapMarker(markerObj)
            map.addMarker(marker) { result ->
                val markerId = result.getOrThrow()

                val res = JSObject()
                res.put("id", markerId)
                call.resolve(res)
            }
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun addMarkers(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val markerObjectArray = call.getArray("markers", null)
            markerObjectArray ?: throw InvalidArgumentsError("markers array is missing")

            if (markerObjectArray.length() == 0) {
                throw InvalidArgumentsError("markers array requires at least one marker")
            }

            val map = maps[id]
            map ?: throw MapNotFoundError()

            val markers: MutableList<CapacitorGoogleMapMarker> = mutableListOf()

            for (i in 0 until markerObjectArray.length()) {
                val markerObj = markerObjectArray.getJSONObject(i)
                val marker = CapacitorGoogleMapMarker(markerObj)

                markers.add(marker)
            }

            map.addMarkers(markers) { result ->
                val ids = result.getOrThrow()

                val jsonIDs = JSONArray()
                ids.forEach { jsonIDs.put(it) }

                val res = JSObject()
                res.put("ids", jsonIDs)
                call.resolve(res)
            }
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun enableClustering(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val map = maps[id]
            map ?: throw MapNotFoundError()

            map.enableClustering { err ->
                if (err != null) {
                    throw err
                }

                call.resolve()
            }
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun disableClustering(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val map = maps[id]
            map ?: throw MapNotFoundError()

            map.disableClustering { err ->
                if (err != null) {
                    throw err
                }

                call.resolve()
            }
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun removeMarker(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val markerId = call.getString("markerId")
            markerId ?: throw InvalidArgumentsError("markerId is invalid or missing")

            val map = maps[id]
            map ?: throw MapNotFoundError()

            map.removeMarker(markerId) { err ->
                if (err != null) {
                    throw err
                }

                call.resolve()
            }
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun removeMarkers(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val markerIdsArray = call.getArray("markerIds")
            markerIdsArray ?: throw InvalidArgumentsError("markerIds are invalid or missing")

            if (markerIdsArray.length() == 0) {
                throw InvalidArgumentsError("markerIds requires at least one marker id")
            }

            val map = maps[id]
            map ?: throw MapNotFoundError()

            val markerIds: MutableList<String> = mutableListOf()

            for (i in 0 until markerIdsArray.length()) {
                val markerId = markerIdsArray.getString(i)
                markerIds.add(markerId)
            }

            map.removeMarkers(markerIds) { err ->
                if (err != null) {
                    throw err
                }

                call.resolve()
            }
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun setCamera(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val map = maps[id]
            map ?: throw MapNotFoundError()

            val cameraConfigObject =
                    call.getObject("config")
                            ?: throw InvalidArgumentsError("config object is missing")

            val config = GoogleMapCameraConfig(cameraConfigObject)

            map.setCamera(config) { err ->
                if (err != null) {
                    throw err
                }

                call.resolve()
            }
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun setMapType(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val map = maps[id]
            map ?: throw MapNotFoundError()

            val mapType =
                    call.getString("mapType") ?: throw InvalidArgumentsError("mapType is missing")

            map.setMapType(mapType) { err ->
                if (err != null) {
                    throw err
                }

                call.resolve()
            }
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun enableIndoorMaps(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val map = maps[id]
            map ?: throw MapNotFoundError()

            val enabled =
                    call.getBoolean("enabled") ?: throw InvalidArgumentsError("enabled is missing")

            map.enableIndoorMaps(enabled) { err ->
                if (err != null) {
                    throw err
                }

                call.resolve()
            }
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun enableTrafficLayer(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val map = maps[id]
            map ?: throw MapNotFoundError()

            val enabled =
                    call.getBoolean("enabled") ?: throw InvalidArgumentsError("enabled is missing")

            map.enableTrafficLayer(enabled) { err ->
                if (err != null) {
                    throw err
                }

                call.resolve()
            }
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun enableCurrentLocation(call: PluginCall) {
        if (getPermissionState(LOCATION) != PermissionState.GRANTED) {
            requestAllPermissions(call, "enableCurrentLocationCallback")
        } else {
            internalEnableCurrentLocation(call)
        }
    }

    @PermissionCallback
    fun enableCurrentLocationCallback(call: PluginCall) {
        if (getPermissionState(LOCATION) == PermissionState.GRANTED) {
            internalEnableCurrentLocation(call)
        } else {
            call.reject("location permission was denied")
        }
    }

    @PluginMethod
    fun setPadding(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val map = maps[id]
            map ?: throw MapNotFoundError()

            val paddingObj =
                    call.getObject("padding") ?: throw InvalidArgumentsError("padding is missing")

            val padding = GoogleMapPadding(paddingObj)

            map.setPadding(padding) { err ->
                if (err != null) {
                    throw err
                }

                call.resolve()
            }
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun enableAccessibilityElements(call: PluginCall) {
        call.unavailable("this call is not available on android")
    }

    @PluginMethod
    fun onScroll(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val map = maps[id]
            map ?: throw MapNotFoundError()

            val boundsObj =
                    call.getObject("mapBounds")
                            ?: throw InvalidArgumentsError("mapBounds object is missing")

            val bounds = boundsObjectToRect(boundsObj)

            map.updateRender(bounds)

            call.resolve()
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun dispatchMapEvent(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val map = maps[id]
            map ?: throw MapNotFoundError()

            val focus = call.getBoolean("focus", false)!!

            val events = cachedTouchEvents[id]
            if (events != null) {
                for (event in events) {
                    if (focus) {
                        map.dispatchTouchEvent(event)
                    } else {
                        event.source = -1
                        this.bridge.webView.dispatchTouchEvent(event)
                    }
                }
            }

            cachedTouchEvents[id]?.clear()

            call.resolve()
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun getMapBounds(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val map = maps[id]
            map ?: throw MapNotFoundError()

            CoroutineScope(Dispatchers.Main).launch {
                val bounds = map.getLatLngBounds()
                val data = map.getLatLngBoundsJSObject(bounds)
                call.resolve(data)
            }
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    private fun internalEnableCurrentLocation(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val map = maps[id]
            map ?: throw MapNotFoundError()

            val enabled =
                    call.getBoolean("enabled") ?: throw InvalidArgumentsError("enabled is missing")

            map.enableCurrentLocation(enabled) { err ->
                if (err != null) {
                    throw err
                }

                call.resolve()
            }
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    fun notify(event: String, data: JSObject) {
        notifyListeners(event, data)
    }

    private fun handleError(call: PluginCall, e: Exception) {
        val error: GoogleMapErrorObject = getErrorObject(e)
        Log.w(tag, error.toString())
        call.reject(error.message, error.code.toString(), e)
    }

    private fun handleError(call: PluginCall, e: GoogleMapsError) {
        val error: GoogleMapErrorObject = getErrorObject(e)
        Log.w(tag, error.toString())
        call.reject(error.message, error.code.toString())
    }

    private fun boundsObjectToRect(jsonObject: JSONObject): RectF {
        if (!jsonObject.has("width")) {
            throw InvalidArgumentsError(
                    "GoogleMapConfig object is missing the required 'width' property"
            )
        }

        if (!jsonObject.has("height")) {
            throw InvalidArgumentsError(
                    "GoogleMapConfig object is missing the required 'height' property"
            )
        }

        if (!jsonObject.has("x")) {
            throw InvalidArgumentsError(
                    "GoogleMapConfig object is missing the required 'x' property"
            )
        }

        if (!jsonObject.has("y")) {
            throw InvalidArgumentsError(
                    "GoogleMapConfig object is missing the required 'y' property"
            )
        }

        val width = jsonObject.getDouble("width")
        val height = jsonObject.getDouble("height")
        val x = jsonObject.getDouble("x")
        val y = jsonObject.getDouble("y")

        return RectF(x.toFloat(), y.toFloat(), (x + width).toFloat(), (y + height).toFloat())
    }
}
