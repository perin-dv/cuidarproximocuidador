package com.mesawa.cuidarproximocuidador.ui.perfil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

class CropPhotoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0x99000000.toInt() }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    private val matrix = Matrix()
    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private val cropRect = RectF()
    private var bitmap: Bitmap? = null
    private var lastX = 0f
    private var lastY = 0f
    private var dragging = false
    private var currentScale = 1f

    fun setBitmap(value: Bitmap) {
        bitmap = value
        post { resetImage() }
    }

    fun crop(size: Int = 720): Bitmap {
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val scale = size / cropRect.width()
        canvas.translate(-cropRect.left * scale, -cropRect.top * scale)
        canvas.scale(scale, scale)
        drawImageOnly(canvas)
        return output
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawImageOnly(canvas)

        val outer = Path().apply { addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW) }
        val inner = Path().apply { addOval(cropRect, Path.Direction.CW) }
        outer.op(inner, Path.Op.DIFFERENCE)
        canvas.drawPath(outer, overlayPaint)
        canvas.drawOval(cropRect, borderPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
                dragging = true
            }
            MotionEvent.ACTION_MOVE -> {
                if (dragging && !scaleDetector.isInProgress) {
                    matrix.postTranslate(event.x - lastX, event.y - lastY)
                    lastX = event.x
                    lastY = event.y
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> dragging = false
        }
        return true
    }

    private fun drawImageOnly(canvas: Canvas) {
        bitmap?.let { canvas.drawBitmap(it, matrix, imagePaint) }
    }

    private fun resetImage() {
        val source = bitmap ?: return
        val size = minOf(width, height) * 0.76f
        cropRect.set((width - size) / 2f, (height - size) / 2f, (width + size) / 2f, (height + size) / 2f)
        val baseScale = maxOf(cropRect.width() / source.width, cropRect.height() / source.height)
        currentScale = baseScale
        matrix.reset()
        matrix.postScale(baseScale, baseScale)
        matrix.postTranslate(
            cropRect.centerX() - source.width * baseScale / 2f,
            cropRect.centerY() - source.height * baseScale / 2f
        )
        invalidate()
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val factor = detector.scaleFactor.coerceIn(0.85f, 1.18f)
            val next = (currentScale * factor).coerceIn(0.35f, 8f)
            val realFactor = next / currentScale
            currentScale = next
            matrix.postScale(realFactor, realFactor, detector.focusX, detector.focusY)
            invalidate()
            return true
        }
    }
}
