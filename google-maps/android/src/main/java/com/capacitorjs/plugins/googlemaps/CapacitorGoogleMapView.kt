package com.capacitorjs.plugins.googlemaps
import android.content.Context
import android.graphics.*
import android.os.Build
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView

class CapacitorGoogleMapView(context: Context, options: GoogleMapOptions?, displayDensity: Float) : MapView(context, options) {
    private var clipPath = Path()
    private var frameRect = RectF()
    private var mapRect = RectF()
    private var scale = displayDensity

    fun updateBounds(bounds: RectF, frameBounds: RectF) {
        frameRect = getScaledRect(frameBounds)
        mapRect = getScaledRect(bounds)

        this.x = mapRect.left
        this.y = mapRect.top

        val clipRects = arrayListOf<RectF>()

        if (!frameRect.contains(mapRect)) {
            clipRects.addAll(this.getFrameOverflowBounds())
        }

        if (clipRects.isNotEmpty()) {
            clipPath = Path()

            clipRects.forEach {
                clipPath.addRect(it, Path.Direction.CW)
            }
        }

        this.invalidate()
    }

    fun getFrameOverflowBounds(): ArrayList<RectF> {
        val intersections = arrayListOf<RectF>()

        if(mapRect.top < frameRect.top) {
            val height = frameRect.top - mapRect.top
            val width = mapRect.width()

            intersections.add(RectF(0f, 0f, width, height))
        }

        if (mapRect.bottom > frameRect.bottom) {
            val height = mapRect.bottom - frameRect.bottom
            val width = mapRect.width()

            intersections.add(RectF(0f, mapRect.height().toFloat(), width, mapRect.height().toFloat() + height))
        }

        return intersections
    }

    override fun onDraw(canvas: Canvas?) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas?.clipOutPath(clipPath)
        } else {
            canvas?.clipPath(clipPath, Region.Op.DIFFERENCE)
        }

        super.onDraw(canvas)
    }

    private fun getScaledPixels(pixels: Float): Float {
        // Convert the dps to pixels, based on density scale
        return (pixels * scale + 0.5f)
    }

    private fun getScaledRect(rectF: RectF): RectF {
        return RectF(
            getScaledPixels(rectF.left),
            getScaledPixels(rectF.top),
            getScaledPixels(rectF.right),
            getScaledPixels(rectF.bottom)
        )
    }
}