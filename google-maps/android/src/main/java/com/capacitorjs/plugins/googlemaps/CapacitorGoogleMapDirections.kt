package com.capacitorjs.plugins.googlemaps
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
class CapacitorGoogleMapDirections(fromJSONObject: JSONObject) {
    var result:JSONObject? = null


    init {
        if (fromJSONObject.has("result")){
            result = fromJSONObject.getJSONObject("result")
        }
        println(result)
    }


}