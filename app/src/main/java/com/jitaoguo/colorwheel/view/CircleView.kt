package com.jitaoguo.colorwheel.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jitaoguo.colorwheel.R

class CircleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private val notSelectedColor = context.getColor(R.color.not_selected)
    private val selectedColor = context.getColor(R.color.selected)

    var color = 0

    init {
        paint.apply {
            isAntiAlias = true
            color = notSelectedColor
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(if (isSelected) selectedColor else notSelectedColor)
        paint.color = color
        canvas.drawCircle(width / 2f, height / 2f, RADIUS, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val mode = MeasureSpec.getMode(widthMeasureSpec)
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(width, mode),
            MeasureSpec.makeMeasureSpec(width, mode)
        )
    }

    companion object {
        private const val RADIUS = 10f
    }
}