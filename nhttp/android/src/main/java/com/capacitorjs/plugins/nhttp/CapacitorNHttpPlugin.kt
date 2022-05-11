package com.capacitorjs.plugins.nhttp

import android.Manifest
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.annotation.Permission


@CapacitorPlugin(
    name = "CapacitorNHttp",
    permissions = [
        Permission(
            strings = [Manifest.permission.READ_EXTERNAL_STORAGE],
            alias = CapacitorNHttpPlugin.HTTP_READ
        ),
        Permission(
            strings = [Manifest.permission.WRITE_EXTERNAL_STORAGE],
            alias = CapacitorNHttpPlugin.HTTP_WRITE
        ),
    ],
)
class CapacitorNHttpPlugin : Plugin() {
    companion object {
        const val HTTP_READ = "HttpRead"
        const val HTTP_WRITE = "HttpWrite"
    }

    @PluginMethod
    fun fetch(call: PluginCall) {
        val requestHandler = HttpRequestHandler.getInstance(this.context)
        val request = requestHandler.createRequestFromCall(call)
        requestHandler.addToRequestQueue(request)
    }
}