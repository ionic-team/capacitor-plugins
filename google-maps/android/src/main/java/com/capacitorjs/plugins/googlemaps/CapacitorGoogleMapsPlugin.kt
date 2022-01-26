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

            if (null == id || id.length <= 0) {
                val error: GoogleMapErrorObject = getErrorObject(GoogleMapErrors.INVALID_MAP_ID)
                Log.w(TAG, error.toString())
                call.reject(error.toString())
                return
            }

            val configObject = call.getObject("config")

            if(null == configObject) {
                val error: GoogleMapErrorObject = getErrorObject(GoogleMapErrors.INVALID_ARGUMENTS,
                    "GoogleMapConfig is missing")
                Log.w(TAG, error.toString())
                call.reject(error.toString())
                return
            }

            val forceCreate = call.getBoolean("forceCreate", false)!!
            val config = GoogleMapConfig(configObject)

            if(null != config.error) {
                Log.w(TAG, config.error.toString())
                call.reject(config.error.toString())
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
            maps.put(id!!, newMap)

            call.resolve()
        }
        catch(e: Exception) {
            Log.w(TAG, e)
            call.reject(e.message)
        }
    }

    @PluginMethod
    fun destroy(call: PluginCall) {
        try {
            val id = call.getString("id")

            if (null == id || id.isEmpty()) {
                val error: GoogleMapErrorObject = getErrorObject(GoogleMapErrors.INVALID_MAP_ID)
                Log.w(TAG, error.toString())
                call.reject(error.toString())
                return
            }

            val removedMap = maps.remove(id)

            if(null ==removedMap) {
                val error: GoogleMapErrorObject = getErrorObject(GoogleMapErrors.MAP_NOT_FOUND)
                Log.w(TAG, error.toString())
                call.reject(error.toString())
                return
            }

            call.resolve()
        }
        catch(e: Exception) {
            Log.w(TAG, e)
            call.reject(e.message)
        }
    }
}