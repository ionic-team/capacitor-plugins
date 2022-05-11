package com.capacitorjs.plugins.nhttp

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.getcapacitor.JSObject
import com.getcapacitor.PluginCall

class HttpRequestHandler constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: HttpRequestHandler? = null
        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: HttpRequestHandler(context).also {
                INSTANCE = it
            }
        }
    }

    private val tag: String = "CAPACITOR-NHTTP"

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    fun createRequestFromCall(call: PluginCall): HttpRequest {
        val resource = call.getObject("resource", null) ?: call.getString("resource")

        if(resource == null) {
            Log.w(tag, "Invalid resource sent from bridge.")
            call.reject("Invalid resource sent from bridge.")
        }

        val config = call.getObject("config", null)

        val methodType =
            if (resource is JSObject) getMethodType(resource.getString("method"))
            else if (config != null)  getMethodType(config.getString("method"))
            else  Request.Method.GET

        val url =
            if (resource is String) resource
            else (resource as JSObject).getString("url", "")!!

        val headers =
            if (resource is JSObject) resource.getJSObject("headers")
            else config?.getJSObject("headers")

        val body =
            if (resource is JSObject) resource.getJSObject("body")
            else config?.getJSObject("body")

        return HttpRequest(
            methodType,
            url,
            headers,
            body,
            { response ->
                call.resolve(JSObject.fromJSONObject(response))
            },
            { error ->
                Log.w(tag, error.toString())
                call.reject(error.toString())
            }
        )
    }

    private fun getMethodType(method: String?): Int {
        when (method) {
            "DELETE" -> return Request.Method.DELETE
            "GET" -> return Request.Method.GET
            "POST" -> return Request.Method.POST
            "PUT" -> return Request.Method.PUT
            else -> return Request.Method.GET
        }
    }
}
