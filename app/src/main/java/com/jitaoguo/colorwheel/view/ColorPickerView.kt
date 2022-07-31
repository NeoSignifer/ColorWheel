package com.jitaoguo.colorwheel.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import kotlin.jvm.JvmOverloads
import android.view.MotionEvent
import android.util.AttributeSet
import android.view.View
import com.jitaoguo.colorwheel.R
import kotlin.math.atan2
import kotlin.math.sqrt

class ColorPickerView @JvmOverloads constructor(
    mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(mContext, attrs, defStyleAttr) {
    private val mPaint: Paint = Paint()
    private val colorWheelPaint: Paint
    private val markerPaint: Paint
    private lateinit var colorWheelBitmap: Bitmap
    private var markerBitmap: Bitmap
    private val currentPoint = PointF()
    private val markerPoint = PointF()
    private var centerX = 0
    private var centerY = 0
    private var colorWheelRadius = 0
    lateinit var mColorWheelRect: Rect
    private var currentColor = 0
    private var centerWheelX = 0
    private var centerWheelY = 0

    init {
        mPaint.alpha = 100
        colorWheelPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }
        markerPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }
        markerBitmap = BitmapFactory.decodeResource(resources, R.mipmap.combined_shape)
    }

    override fun onDraw(canvas: Canvas) {
        drawColorWheel(canvas)
        drawMarker(canvas)
    }

    private fun drawColorWheel(canvas: Canvas) {
        canvas.drawBitmap(
            colorWheelBitmap,
            mColorWheelRect.left.toFloat(),
            mColorWheelRect.top.toFloat(),
            null
        )
    }

    private fun drawMarker(canvas: Canvas) {
        val markerWidth = markerBitmap.width.toFloat()
        val markerHeight = markerBitmap.height.toFloat()
        // marker position
        val left = markerPoint.x - markerWidth / 2
        val top = markerPoint.y - markerHeight + markerHeight * 1 / 10
        val dst = RectF(left, top, left + markerWidth, top + markerHeight)

        val markerRadius = markerWidth / 3
        markerPaint.color = currentColor //set color
        canvas.drawBitmap(markerBitmap, null, dst, null)
        canvas.drawCircle(
            left + markerWidth / 2,
            top + markerWidth / 2 - 4,
            markerRadius,
            markerPaint
        )
    }

    override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        var width = width
        centerX = width / 2
        centerY = height / 2
        if (height < width) { //make it a square
            width = height
        }
        colorWheelRadius = (width * RADIUS_WIDTH_RATIO).toInt()
        mColorWheelRect = Rect(
            centerX - colorWheelRadius,
            centerY - colorWheelRadius,
            centerX + colorWheelRadius,
            centerY + colorWheelRadius
        )
        colorWheelBitmap = createColorWheelBitmap(colorWheelRadius * 2, colorWheelRadius * 2)
        //color center location
        centerWheelX = mColorWheelRect.left + colorWheelRadius
        centerWheelY = mColorWheelRect.top + colorWheelRadius
        //default marker is center
        markerPoint.x = centerWheelX.toFloat()
        markerPoint.y = centerWheelY.toFloat()
    }


    private fun createColorWheelBitmap(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val colorCount = 12
        val colorAngleStep = 360 / colorCount
        val colors = IntArray(colorCount + 1)
        val hsv = floatArrayOf(0f, 1f, 1f)
        for (i in colors.indices) {
            hsv[0] = (360 - i * colorAngleStep % 360).toFloat()
            colors[i] = Color.HSVToColor(hsv)
        }
        colors[colorCount] = colors[0]
        val sweepGradient = SweepGradient(width / 2f, height / 2f, colors, null)
        val radialGradient = RadialGradient(
            width / 2f,
            height / 2f,
            colorWheelRadius.toFloat(),
            -0x1,
            0x00FFFFFF,
            Shader.TileMode.CLAMP
        )
        val composeShader = ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER)
        colorWheelPaint.shader = composeShader
        val canvas = Canvas(bitmap)
        canvas.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            colorWheelRadius.toFloat(),
            colorWheelPaint
        )

        //default is center
        currentColor = getColorAtPoint(markerPoint.x, markerPoint.y)
        return bitmap
    }

    private val downPointF = PointF() //point down

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                colorTmp = currentColor
                downPointF.x = event.x
                downPointF.y = event.y
                update(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                update(event)
                return true
            }
            MotionEvent.ACTION_UP -> if (colorTmp != currentColor) {
                onColorPickerChanged()
            }
            else -> return true
        }
        return super.onTouchEvent(event)
    }

    private fun update(event: MotionEvent) {
        val x = event.x
        val y = event.y
        updateSelector(x, y)
    }

    private fun getColorAtPoint(eventX: Float, eventY: Float): Int {
        val x = eventX - centerWheelX
        val y = eventY - centerWheelY
        val r = sqrt((x * x + y * y).toDouble())
        val hsv = floatArrayOf(0f, 0f, 1f)
        hsv[0] = (atan2(y.toDouble(), -x.toDouble()) / Math.PI * 180f).toFloat() + 180
        hsv[1] = 0f.coerceAtLeast(1f.coerceAtMost((r / colorWheelRadius).toFloat()))
        return Color.HSVToColor(hsv)
    }

    private fun updateSelector(eventX: Float, eventY: Float) {
        val x = eventX - centerWheelX
        val y = eventY - centerWheelY
        val r = sqrt((x * x + y * y).toDouble())
        if (r > colorWheelRadius) {
            // out of scope
            return
        }
        currentPoint.x = x + centerWheelX
        currentPoint.y = y + centerWheelY
        markerPoint.x = currentPoint.x
        markerPoint.y = currentPoint.y
        currentColor = getColorAtPoint(eventX, eventY)
        invalidate()
    }

    private fun onColorPickerChanged() {
        onColorPickerChangerListener?.let {
            val red = currentColor and 0xff0000 shr 16
            val green = currentColor and 0x00ff00 shr 8
            val blue = currentColor and 0x0000ff
            it.onColorPickerChanger(currentColor, red, green, blue)
        }
    }

    var color: Int
        get() = currentColor
        set(color) {
            val hsv = floatArrayOf(0f, 0f, 1f)
            Color.colorToHSV(color, hsv)
            // get location from hsv degrees and radius
            val radian = Math.toRadians((-hsv[0]).toDouble()).toFloat()
            val colorDotRadius = hsv[1] * colorWheelRadius
            val colorDotX = (centerWheelX + Math.cos(radian.toDouble()) * colorDotRadius).toFloat()
            val colorDotY = (centerWheelY + Math.sin(radian.toDouble()) * colorDotRadius).toFloat()
            //set marker location
            markerPoint.x = colorDotX
            markerPoint.y = colorDotY
            currentColor = getColorAtPoint(markerPoint.x, markerPoint.y) //set current color
            invalidate()
        }

    val colorRGB: IntArray
        get() = intArrayOf(
            currentColor and 0xff0000 shr 16,
            currentColor and 0x00ff00 shr 8,
            currentColor and 0x0000ff
        )

    private var onColorPickerChangerListener: OnColorPickerChangerListener? = null

    fun setOnColorPickerChangerListener(onColorPickerChangerListener: OnColorPickerChangerListener?) {
        this.onColorPickerChangerListener = onColorPickerChangerListener
    }

    interface OnColorPickerChangerListener {
        fun onColorPickerChanger(currentColor: Int, red: Int, green: Int, blue: Int)
    }

    companion object {
        private const val RADIUS_WIDTH_RATIO = 0.5
        private var colorTmp = 0
    }

}