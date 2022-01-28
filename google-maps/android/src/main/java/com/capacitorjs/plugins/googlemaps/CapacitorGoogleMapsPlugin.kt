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

            call.resolve()
        }
        catch(e: Exception) {
            handleError(call, e)
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
}