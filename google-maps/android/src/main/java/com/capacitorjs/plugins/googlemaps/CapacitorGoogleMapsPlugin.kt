package com.capacitorjs.plugins.googlemaps

import android.util.Log
import com.getcapacitor.JSObject
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

    @PluginMethod
    fun addMarker(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val markerObj = call.getObject("marker", null)
            markerObj ?: throw InvalidArgumentsError("Marker object is missing")

            val map = maps[id]
            map ?: throw MapNotFoundError()

            val marker = CapacitorGoogleMapMarker(markerObj)
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
            id ?: throw InvalidMapIdError()

            val markerId = call.getString("markerId")
            markerId ?: throw InvalidArgumentsError("marker id is invalid or missing")

            val map = maps[id]
            map ?: throw MapNotFoundError()

            map.removeMarker(markerId)

            call.resolve()
        } catch(e: Exception) {
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
