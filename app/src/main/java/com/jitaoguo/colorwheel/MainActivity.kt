package com.jitaoguo.colorwheel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jitaoguo.colorwheel.view.ColorPickerView
import com.jitaoguo.colorwheel.view.ThreeColorPickerView

class MainActivity : AppCompatActivity() {

    var colorPickerView: ColorPickerView? = null
    var threeColorPickerView: ThreeColorPickerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        colorPickerView = findViewById(R.id.color_picker_view)
        threeColorPickerView = findViewById(R.id.three_color_picker)
    }

    override fun onResume() {
        super.onResume()
        colorPickerView?.setOnColorPickerChangerListener(object :
            ColorPickerView.OnColorPickerChangerListener {
            override fun onColorPickerChanger(currentColor: Int, red: Int, green: Int, blue: Int) {
                threeColorPickerView?.setCurrentSelectedColor(currentColor)
            }
        })

        threeColorPickerView?.setOnSelectedColorChangeListener(object :
            ThreeColorPickerView.OnSelectedColorChangeListener {
            override fun onSelectedColorChange(color: Int) {
                colorPickerView?.color = color
            }
        })
    }
}