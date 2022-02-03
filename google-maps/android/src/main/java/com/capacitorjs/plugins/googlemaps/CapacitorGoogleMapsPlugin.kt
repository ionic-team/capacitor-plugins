package com.capacitorjs.plugins.googlemaps

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.google.android.libraries.maps.MapView

@CapacitorPlugin(name = "CapacitorGoogleMaps")
class CapacitorGoogleMapsPlugin : Plugin() {
    var maps: HashMap<String, GoogleMap> = HashMap()
    val TAG: String = "CAP-GOOGLE-MAPS"

    @PluginMethod
    fun create(call: PluginCall) {
        try {
            val id = call.getString("id")

            if (null == id || id.isEmpty()) {
                handleError(call, GoogleMapErrors.INVALID_MAP_ID)
                return
            }

            val configObject = call.getObject("config")

            if (null == configObject) {
                handleError(call, GoogleMapErrors.INVALID_ARGUMENTS, "GoogleMapConfig is missing")
                return
            }

            val forceCreate = call.getBoolean("forceCreate", false)!!

            val config: GoogleMapConfig

            try {
                config = GoogleMapConfig(configObject)
            } catch (e: Exception) {
                handleError(call, GoogleMapErrors.INVALID_ARGUMENTS, e.message)
                return
            }

            if (maps.contains(id)) {
                if (!forceCreate) {
                    call.resolve()
                    return
                }

                maps.remove(id)
            }

            val newMap = GoogleMap(config)
            maps[id] = newMap

            renderMap(newMap, id)

            call.resolve()
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun destroy(call: PluginCall) {
        try {
            val id = call.getString("id")

            if (null == id || id.isEmpty()) {
                handleError(call, GoogleMapErrors.INVALID_MAP_ID)
                return
            }

            val removedMap = maps.remove(id)

            if (null == removedMap) {
                handleError(call, GoogleMapErrors.MAP_NOT_FOUND)
                return
            }

            bridge.activity.runOnUiThread {
                destroyMapInView(id)
                removedMap.mapView!!.onDestroy()
                call.resolve()
            }
        } catch (e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun addMarker(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw Exception("invalid map id")

            val markerObj = call.getObject("marker", null)
            markerObj ?: throw InvalidArgumentsError("Marker object is missing")

            val map = maps[id]
            map ?: throw Exception("Map not found")

            val marker = Marker(markerObj)
            val markerId = map.addMarker(marker)

            val res = JSObject()
            res.put("id", markerId)
            call.resolve(res)
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch(e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun removeMarker(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw Exception("invalid map id")

            val markerId = call.getString("markerId")
            markerId ?: throw InvalidArgumentsError("marker id is invalid or missing")

            val map = maps[id]
            map ?: throw Exception("Map not found")

            map.removeMarker(markerId)

            call.resolve()
        } catch(e: Exception) {
            handleError(call, e)
        }
    }

    private fun destroyMapInView(tag: String) {
        val viewToRemove: View? = ((bridge.webView.parent) as ViewGroup).findViewWithTag(tag)
        if (null != viewToRemove) {
            ((bridge.webView.parent) as ViewGroup).removeView(viewToRemove)
        }
    }

    private fun renderMap(googleMap: GoogleMap, tag: String) {
        bridge.activity.runOnUiThread {
            if (null != googleMap.mapView) {
                destroyMapInView(tag)
            }

            val mapViewParent = FrameLayout(bridge.context)
            val layoutParams =
                    FrameLayout.LayoutParams(
                            getScaledPixels(googleMap.config.width),
                            getScaledPixels(googleMap.config.height),
                    )
            layoutParams.leftMargin = getScaledPixels(googleMap.config.x)
            layoutParams.topMargin = getScaledPixels(googleMap.config.y)

            val mapView = MapView(bridge.context, googleMap.config.googleMapOptions)

            mapViewParent.tag = tag
            mapView.layoutParams = layoutParams
            mapViewParent.addView(mapView)

            ((bridge.webView.parent) as ViewGroup).addView(mapViewParent)

            googleMap.mapView = mapView

            mapView.onCreate(null)
            mapView.onStart()
        }
    }

    private fun handleError(call: PluginCall, e: Exception) {
        val error: GoogleMapErrorObject = getErrorObject(e)
        Log.w(TAG, error.toString())
        call.reject(e.message, error.toString(), e)
    }

    private fun handleError(call: PluginCall, e: GoogleMapsError) {
        val error: GoogleMapErrorObject = getErrorObject(e)
        Log.w(TAG, error.toString())
        call.reject(e.message, error.toString(), e)
    }

    private fun handleError(call: PluginCall, errorEnum: GoogleMapErrors, message: String? = "") {
        val error: GoogleMapErrorObject = getErrorObject(errorEnum, message)
        handleError(call, error)
    }

    private fun handleError(call: PluginCall, error: GoogleMapErrorObject) {
        Log.w(TAG, error.toString())
        call.reject(error.message, error.toString())
    }

    private fun getScaledPixels(pixels: Int): Int {
        // Get the screen's density scale
        val scale = bridge.activity.resources.displayMetrics.density
        // Convert the dps to pixels, based on density scale
        return (pixels * scale + 0.5f).toInt()
    }
}
