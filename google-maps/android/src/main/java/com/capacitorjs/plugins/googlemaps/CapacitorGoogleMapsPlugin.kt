package com.capacitorjs.plugins.googlemaps

import android.util.Log
import com.getcapacitor.Bridge
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin

@CapacitorPlugin(name = "CapacitorGoogleMaps")
class CapacitorGoogleMapsPlugin : Plugin() {
    private var maps: HashMap<String, CapacitorGoogleMap> = HashMap()
    private val tag: String = "CAP-GOOGLE-MAPS"

    @PluginMethod
    fun create(call: PluginCall) {
        try {
            val id = call.getString("id")

            if (null == id || id.isEmpty()) {
                throw InvalidMapIdError()
            }

            val configObject = call.getObject("config")
                ?: throw InvalidArgumentsError("GoogleMapConfig is missing")

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
        }
        catch(e: GoogleMapsError) {
            handleError(call, e)
        }
        catch (e: Exception) {
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
        }
        catch(e: GoogleMapsError) {
            handleError(call, e)
        }
        catch (e: Exception) {
            handleError(call, e)
        }
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
}
