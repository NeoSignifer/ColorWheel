package com.jitaoguo.colorwheel.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.jitaoguo.colorwheel.R

class ThreeColorPickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var selectedIndex = 0
    private val colorViews = mutableListOf<CircleView>()
    private var listener: OnSelectedColorChangeListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_three_color_picker, this)
        initChildren()
    }

    private fun initChildren() {
        colorViews.add(findViewById<CircleView>(R.id.first_color_view).apply {
            color = context.getColor(R.color.teal)
            isSelected = true
            setOnClickListener {
                updateSelectedChild(0)
            }
        })
        colorViews.add(findViewById<CircleView>(R.id.second_color_view).apply {
            color = context.getColor(R.color.green)
            setOnClickListener {
                updateSelectedChild(1)
            }
        })
        colorViews.add(findViewById<CircleView>(R.id.third_color_view).apply {
            color = context.getColor(R.color.orange)
            setOnClickListener {
                updateSelectedChild(2)
            }
        })
    }

    private fun updateSelectedChild(nextSelectedIndex: Int) {
        if (selectedIndex == nextSelectedIndex) {
            return
        }
        colorViews.forEachIndexed { index, circleView ->
            circleView.isSelected = index == nextSelectedIndex
        }
        selectedIndex = nextSelectedIndex
        val selectedColor = colorViews[selectedIndex].color
        listener?.onSelectedColorChange(selectedColor)
    }

    fun setCurrentSelectedColor(color: Int) {
        colorViews[selectedIndex].color = color
        colorViews[selectedIndex].invalidate()
    }

    fun setOnSelectedColorChangeListener(listener: OnSelectedColorChangeListener) {
        this.listener = listener
    }

    fun getSelectedColor() = colorViews[selectedIndex].color

    interface OnSelectedColorChangeListener {
        fun onSelectedColorChange(color: Int)
    }

}