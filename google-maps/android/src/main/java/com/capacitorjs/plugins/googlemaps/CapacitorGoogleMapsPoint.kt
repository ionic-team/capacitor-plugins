package com.capacitorjs.plugins.googlemaps

import org.json.JSONObject

class CapacitorGoogleMapsPoint() {
    var x: Float = 0.00f
    var y: Float = 0.00f

    constructor(fromJSONObject: JSONObject) : this() {
        if(fromJSONObject.has("x")) {
            this.x = fromJSONObject.getDouble("x").toFloat()
        }

        if(fromJSONObject.has("y")) {
            this.x = fromJSONObject.getDouble("y").toFloat()
        }
    }

    constructor(x: Float, y: Float) : this() {
        this.x = x;
        this.y = y
    }
}