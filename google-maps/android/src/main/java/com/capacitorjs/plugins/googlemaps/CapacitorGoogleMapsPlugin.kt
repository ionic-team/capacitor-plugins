package com.capacitorjs.plugins.googlemaps

import android.Manifest
import android.util.Log
import com.getcapacitor.*
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.annotation.Permission
import com.getcapacitor.annotation.PermissionCallback
import org.json.JSONArray

@CapacitorPlugin(
    name = "CapacitorGoogleMaps",
    permissions = [
        Permission(
            strings = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION],
            alias = CapacitorGoogleMapsPlugin.LOCATION
        ),
        Permission(
            strings = [Manifest.permission.ACCESS_COARSE_LOCATION],
            alias = CapacitorGoogleMapsPlugin.COARSE_LOCATION
        )
    ],
)
class CapacitorGoogleMapsPlugin : Plugin() {
    private var maps: HashMap<String, CapacitorGoogleMap> = HashMap()
    private val tag: String = "CAP-GOOGLE-MAPS"

    companion object {
        const val LOCATION = "location"
        const val COARSE_LOCATION = "coarse_location"
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

    @PluginMethod
    fun setCamera(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val map = maps[id]
            map ?: throw MapNotFoundError()

            val cameraConfigObject = call.getObject("config")
                ?: throw InvalidArgumentsError("GoogleMapCameraConfig is missing")

            val config = GoogleMapCameraConfig(cameraConfigObject)

            map.setCamera(config)

            call.resolve()
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch(e: Exception) {
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

            val mapType = call.getString("mapType")
                ?: throw InvalidArgumentsError("mapType is missing")

            map.setMapType(mapType)

            call.resolve()
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch(e: Exception) {
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

            val enabled = call.getBoolean("enabled")
                ?: throw InvalidArgumentsError("enabled is missing")

            map.enableIndoorMaps(enabled)

            call.resolve()
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch(e: Exception) {
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

            val enabled = call.getBoolean("enabled")
                ?: throw InvalidArgumentsError("enabled is missing")

            map.enableTrafficLayer(enabled)

            call.resolve()
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch(e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun enableCurrentLocation(call: PluginCall) {
        if (getPermissionState(CapacitorGoogleMapsPlugin.LOCATION) != PermissionState.GRANTED) {
            requestAllPermissions(call, "enableCurrentLocationCallback")
        } else {
            internalEnableCurrentLocation(call)
        }
    }

    @PermissionCallback
    fun enableCurrentLocationCallback(call: PluginCall) {
        if (getPermissionState(CapacitorGoogleMapsPlugin.LOCATION) == PermissionState.GRANTED) {
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

            val paddingObj = call.getObject("padding")
                ?: throw InvalidArgumentsError("padding is missing")

            val padding = GoogleMapPadding(paddingObj)

            map.setPadding(padding)

            call.resolve()
        } catch (e: GoogleMapsError) {
            handleError(call, e)
        } catch(e: Exception) {
            handleError(call, e)
        }
    }

    @PluginMethod
    fun enableAccessibilityElements(call: PluginCall) {
        call.unavailable("this call is not available on android")
    }

    private fun internalEnableCurrentLocation(call: PluginCall) {
        try {
            val id = call.getString("id")
            id ?: throw InvalidMapIdError()

            val map = maps[id]
            map ?: throw MapNotFoundError()

            val enabled = call.getBoolean("enabled")
                ?: throw InvalidArgumentsError("enabled is missing")

            map.enableCurrentLocation(enabled)

            call.resolve()
        } catch (e: GoogleMapsError) {
            handleError(call, e)
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
