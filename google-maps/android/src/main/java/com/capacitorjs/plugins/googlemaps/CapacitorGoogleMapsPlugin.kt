package com.capacitorjs.plugins.googlemaps

import android.util.Log
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import org.json.JSONArray

@CapacitorPlugin(name = "CapacitorGoogleMaps")
class CapacitorGoogleMapsPlugin : Plugin() {
    private var maps: HashMap<String, CapacitorGoogleMap> = HashMap()
    private val tag: String = "CAP-GOOGLE-MAPS"

    override fun handleOnStart() {
        super.handleOnStart()
        maps.forEach {
            it.value.onStart()
        }
    }

    override fun handleOnResume() {
        super.handleOnResume()
        maps.forEach {
            it.value.onResume()
        }
    }

    override fun handleOnPause() {
        super.handleOnPause()
        maps.forEach {
            it.value.onPause()
        }
    }

    override fun handleOnStop() {
        super.handleOnStop()
        maps.forEach {
            it.value.onStop()
        }
    }

    override fun handleOnDestroy() {
        super.handleOnDestroy()
        maps.forEach {
            it.value.onDestroy()
        }
    }

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
    fun addMarkers(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val markerObjectArray = call.getArray("markers", null)
            markerObjectArray ?: throw InvalidArgumentsError("Markers array is missing")

            if (markerObjectArray.length() == 0) {
                throw InvalidArgumentsError("Markers array requires at least one marker")
            }

            val map = maps[id]
            map ?: throw MapNotFoundError()

            val markers: MutableList<CapacitorGoogleMapMarker> = mutableListOf()

            for (i in 0 until markerObjectArray.length()) {
                val markerObj = markerObjectArray.getJSONObject(i)
                val marker = CapacitorGoogleMapMarker(markerObj)

                markers.add(marker)
            }

            val ids = map.addMarkers(markers)

            val jsonIDs = JSONArray()
            ids.forEach {
                jsonIDs.put(it)
            }

            val res = JSObject()
            res.put("ids", jsonIDs)
            call.resolve(res)
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch(e: Exception) {
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

            map.enableClustering()

            call.resolve()
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch(e: Exception) {
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

            map.disableClustering()

            call.resolve()
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
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch(e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun removeMarkers(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val markerIdsArray = call.getArray("markerIds")
            markerIdsArray ?: throw InvalidArgumentsError("marker ids are invalid or missing")

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

            map.removeMarkers(markerIds)

            call.resolve()
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch(e: Exception) {
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
}
