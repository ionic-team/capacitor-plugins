package com.capacitorjs.plugins.googlemaps

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
    fun initialize(call: PluginCall) {
        call.resolve()
    }

    @PluginMethod
    fun create(call: PluginCall) {
        try {
            val id = call.getString("id")

            if (null == id || id.isEmpty()) {
                handleError(call, GoogleMapErrors.INVALID_MAP_ID)
                return
            }

            val configObject = call.getObject("config")

            if(null == configObject) {
                handleError(call, GoogleMapErrors.INVALID_ARGUMENTS,
                    "GoogleMapConfig is missing")
                return
            }

            val forceCreate = call.getBoolean("forceCreate", false)!!

            val config: GoogleMapConfig

            try {
                config = GoogleMapConfig(configObject)
            }
            catch (e: Exception) {
                handleError(call, GoogleMapErrors.INVALID_ARGUMENTS, e.message)
                return;
            }

            if(maps.contains(id)) {
                if(!forceCreate) {
                    call.resolve()
                    return
                }

                maps.remove(id)
            }

            val newMap = GoogleMap(config)
            maps[id] = newMap

            renderMap(newMap)

            call.resolve()
        }
        catch(e: Exception) {
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

            if(null == removedMap) {
                handleError(call, GoogleMapErrors.MAP_NOT_FOUND)
                return
            }

            bridge.activity.runOnUiThread {
                destroyMapInView(removedMap.mapViewId!!)
                removedMap.mapView!!.onDestroy()
            }

            call.resolve()
        }
        catch(e: Exception) {
            handleError(call, e)
        }
    }

    private fun destroyMapInView(mapViewId: Int) {
        val viewToRemove: View? = ((bridge.webView.parent) as ViewGroup)
            .findViewById(mapViewId)
        if(null != viewToRemove) {
            ((bridge.webView.parent) as ViewGroup).removeViewInLayout(viewToRemove)
        }
    }

    private fun renderMap(googleMap: GoogleMap) {
        bridge.activity.runOnUiThread {
            var mapViewId: Int? = googleMap.mapViewId

            if (null != mapViewId) {
                destroyMapInView(mapViewId)
            }

            val mapViewParent = FrameLayout(bridge.context)
            val layoutParams = FrameLayout.LayoutParams(
                getScaledPixels(googleMap.config.width),
                getScaledPixels(googleMap.config.height),
            )
            layoutParams.leftMargin = getScaledPixels(googleMap.config.x)
            layoutParams.topMargin = getScaledPixels(googleMap.config.y)

            val mapView = MapView(bridge.context, googleMap.config.googleMapOptions)

            mapViewId = View.generateViewId()
            mapViewParent.id = mapViewId
            mapView.layoutParams = layoutParams
            mapViewParent.addView(mapView)

            ((bridge.webView.parent) as ViewGroup).addView(mapViewParent)

            googleMap.mapViewId = mapViewId
            googleMap.mapView = mapView

            mapView.onCreate(null)
            mapView.onStart()
        }
    }

    private fun handleError(call: PluginCall, e: Exception) {
        val error: GoogleMapErrorObject = getErrorObject(GoogleMapErrors.UNHANDLED_ERROR, e.message)
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