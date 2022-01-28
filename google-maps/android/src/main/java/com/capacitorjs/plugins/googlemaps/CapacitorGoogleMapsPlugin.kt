package com.capacitorjs.plugins.googlemaps

import android.util.Log
import com.getcapacitor.Plugin
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.PluginMethod
import com.getcapacitor.PluginCall

@CapacitorPlugin(name = "CapacitorGoogleMaps")
class CapacitorGoogleMapsPlugin : Plugin() {
    var maps: HashMap<String, GoogleMap> = HashMap()
    val TAG: String = "CAP-GOOGLE-MAPS"

    @PluginMethod
    fun create(call: PluginCall) {
        try {
            val id = call.getString("id")

            if (null == id || id.isEmpty()) {
                val error: GoogleMapErrorObject = getErrorObject(GoogleMapErrors.INVALID_MAP_ID)
                handleError(call, error)
                return
            }

            val configObject = call.getObject("config")

            if(null == configObject) {
                val error: GoogleMapErrorObject = getErrorObject(GoogleMapErrors.INVALID_ARGUMENTS,
                    "GoogleMapConfig is missing")
                handleError(call, error)
                return
            }

            val forceCreate = call.getBoolean("forceCreate", false)!!
            val config = GoogleMapConfig(configObject)

            if(null != config.error) {
                handleError(call, config.error!!)
                return
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
                val error: GoogleMapErrorObject = getErrorObject(GoogleMapErrors.INVALID_MAP_ID)
                handleError(call, error)
                return
            }

            val removedMap = maps.remove(id)

            if(null == removedMap) {
                val error: GoogleMapErrorObject = getErrorObject(GoogleMapErrors.MAP_NOT_FOUND)
                handleError(call, error)
                return
            }

            call.resolve()
        }
        catch(e: Exception) {
            handleError(call, e)
        }
    }

    fun handleError(call: PluginCall, e: Exception) {
        val error: GoogleMapErrorObject = getErrorObject(GoogleMapErrors.UNHANDLED_ERROR, e.message)
        Log.w(TAG, error.toString())
        call.reject(e.message, error.toString(), e)
    }

    fun handleError(call: PluginCall, error: GoogleMapErrorObject) {
        Log.w(TAG, error.toString())
        call.reject(error.message, error.toString())
    }
}