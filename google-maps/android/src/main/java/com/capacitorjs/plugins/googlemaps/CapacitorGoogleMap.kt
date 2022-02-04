package com.capacitorjs.plugins.googlemaps

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.getcapacitor.Bridge
import com.google.android.libraries.maps.MapView

class CapacitorGoogleMap(val id: String, val config: GoogleMapConfig, private val bridge: Bridge) {
    var mapView: MapView? = null

    init {
        render()
    }

    private fun render() {
        bridge.activity.runOnUiThread {
            val mapViewParent = FrameLayout(bridge.context)
            val layoutParams =
                FrameLayout.LayoutParams(
                    getScaledPixels(bridge, this.config.width),
                    getScaledPixels(bridge, this.config.height),
                )
            layoutParams.leftMargin = getScaledPixels(bridge, this.config.x)
            layoutParams.topMargin = getScaledPixels(bridge, this.config.y)

            val mapView = MapView(bridge.context, this.config.googleMapOptions)

            mapViewParent.tag = this.id
            mapView.layoutParams = layoutParams
            mapViewParent.addView(mapView)

            ((bridge.webView.parent) as ViewGroup).addView(mapViewParent)

            this.mapView = mapView

            mapView.onCreate(null)
            mapView.onStart()
        }
    }

    fun destroy() {
        bridge.activity.runOnUiThread {
            val viewToRemove: View? = ((bridge.webView.parent) as ViewGroup).findViewWithTag(this.id)
            if (null != viewToRemove) {
                ((bridge.webView.parent) as ViewGroup).removeView(viewToRemove)
            }
            mapView?.onDestroy()
            mapView = null
        }
    }

    private fun getScaledPixels(bridge: Bridge, pixels: Int): Int {
        // Get the screen's density scale
        val scale = bridge.activity.resources.displayMetrics.density
        // Convert the dps to pixels, based on density scale
        return (pixels * scale + 0.5f).toInt()
    }
}
