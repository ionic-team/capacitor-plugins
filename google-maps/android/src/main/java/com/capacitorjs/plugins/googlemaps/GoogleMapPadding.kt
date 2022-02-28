package com.capacitorjs.plugins.googlemaps

import org.json.JSONObject

class GoogleMapPadding(fromJSONObject: JSONObject) {
    var top: Int = 0
    var bottom: Int = 0
    var left: Int = 0
    var right: Int = 0

    init {
        if(fromJSONObject.has("top")) {
            top = fromJSONObject.getInt("top")
        }

        if(fromJSONObject.has("bottom")) {
            bottom = fromJSONObject.getInt("bottom")
        }

        if(fromJSONObject.has("left")) {
            left = fromJSONObject.getInt("left")
        }

        if(fromJSONObject.has("right")) {
            right = fromJSONObject.getInt("right")
        }
    }
}