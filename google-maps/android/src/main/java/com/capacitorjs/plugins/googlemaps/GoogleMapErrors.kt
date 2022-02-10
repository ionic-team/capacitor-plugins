package com.capacitorjs.plugins.googlemaps

import org.json.JSONObject
import kotlin.Exception

enum class GoogleMapErrors {
    UNHANDLED_ERROR, INVALID_MAP_ID, MAP_NOT_FOUND, MARKER_NOT_FOUND, INVALID_ARGUMENTS
}

class GoogleMapErrorObject(val code: Int, val message: String, val extra: HashMap<String,Any> = HashMap()) {
    private fun asJSONObject(): JSONObject {
        val returnJSONObject = JSONObject()

        returnJSONObject.put("code", code)
        returnJSONObject.put("message", message)
        returnJSONObject.put("extra", extra)

        return returnJSONObject
    }

    override fun toString(): String {
        return this.asJSONObject().toString()
    }
}

fun getErrorObject(err: GoogleMapsError): GoogleMapErrorObject {
    return when(err) {
        is InvalidArgumentsError -> {
            GoogleMapErrorObject(err.getErrorCode(), "Invalid Arguments Provided: ${err.message}.")
        }
        is InvalidMapIdError -> {
            GoogleMapErrorObject(err.getErrorCode(), "Missing or invalid map id.")
        }
        is MapNotFoundError -> {
            GoogleMapErrorObject(err.getErrorCode(), "Map not found for provided id.")
        }
        is MarkerNotFoundError -> {
            GoogleMapErrorObject(err.getErrorCode(), "Marker not found for provided id.")
        }
        else -> {
            GoogleMapErrorObject(err.getErrorCode(), "Unhandled Error: ${err.message}.")
        }
    }
}

fun getErrorObject(err: Exception): GoogleMapErrorObject {
    return GoogleMapErrorObject(0, "Unhandled Error: ${err.message}.")
}

open class GoogleMapsError(message: String? = ""): Throwable(message) {
    open fun getErrorCode(): Int {
        return GoogleMapErrors.UNHANDLED_ERROR.ordinal
    }
}

class InvalidMapIdError(message: String? = ""): GoogleMapsError(message) {
    override fun getErrorCode(): Int {
        return GoogleMapErrors.INVALID_MAP_ID.ordinal
    }
}

class MapNotFoundError(message: String? = ""): GoogleMapsError(message) {
    override fun getErrorCode(): Int {
        return GoogleMapErrors.MAP_NOT_FOUND.ordinal
    }
}

class MarkerNotFoundError(message: String? = ""): GoogleMapsError(message) {
    override fun getErrorCode(): Int {
        return GoogleMapErrors.MARKER_NOT_FOUND.ordinal
    }
}

class InvalidArgumentsError(message: String? = ""): GoogleMapsError(message) {
    override fun getErrorCode(): Int {
        return GoogleMapErrors.INVALID_ARGUMENTS.ordinal
    }
}

