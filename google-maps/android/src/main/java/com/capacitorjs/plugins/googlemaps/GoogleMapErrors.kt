package com.capacitorjs.plugins.googlemaps

import org.json.JSONObject

enum class GoogleMapErrors {
    UNHANDLED_ERROR, INVALID_MAP_ID, MAP_NOT_FOUND, INVALID_ARGUMENTS
}

class GoogleMapErrorObject(val code: Int, val message: String, val extra: HashMap<String,Any> = HashMap()) {
    fun asJSONObject(): JSONObject {
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

fun getErrorObject(errorCode: GoogleMapErrors, description: String? = null): GoogleMapErrorObject {
    when(errorCode) {
        GoogleMapErrors.INVALID_MAP_ID -> {
            return GoogleMapErrorObject(1, "Missing or invalid map id.")
        }
        GoogleMapErrors.MAP_NOT_FOUND -> {
            return GoogleMapErrorObject(2, "Map not found for provided id.")
        }
        GoogleMapErrors.INVALID_ARGUMENTS -> {
            return GoogleMapErrorObject(3, "Invalid Arguments Provided: $description.")
        }
        else -> {
            return GoogleMapErrorObject(GoogleMapErrors.UNHANDLED_ERROR.ordinal, "Unhandled Error: $description.")
        }
    }
}