package com.capacitorjs.plugins.nhttp

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import java.io.UnsupportedEncodingException
import org.json.JSONException
import org.json.JSONObject

class HttpRequest(
        method: Int,
        url: String,
        private val headers: JSONObject?,
        body: JSONObject?,
        responseListener: Response.Listener<JSONObject>,
        errorListener: Response.ErrorListener?,
) : JsonObjectRequest(method, url, body, responseListener, errorListener) {

    override fun getHeaders(): MutableMap<String, String> {
        if (headers != null) {
            val headersMap: MutableMap<String, String> = mutableMapOf()
            val keys = headers.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val value = headers.optString(key)
                headersMap.put(key, value)
            }
            return headersMap
        }
        return super.getHeaders()
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
        return try {
            val jsonString = String(response!!.data, charset("UTF-8"))

            val headersJson = JSONObject()

            val keys = response.headers?.keys
            if (null != keys) {
                for (key in keys) {
                    headersJson.put(key, response.headers!![key])
                }
            }

            val jsonObject = JSONObject()
            jsonObject.put("headers", headersJson)
            jsonObject.put("data", jsonString)
            jsonObject.put("status", response.statusCode)

            Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        } catch (je: JSONException) {
            Response.error(ParseError(je))
        }
    }
}
