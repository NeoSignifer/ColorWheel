package com.jitaoguo.colorwheel.view

import android.content.Context

object DensityUtils {

    fun dp2px(context: Context, dp: Float) = dp.times(context.resources.displayMetrics.density)

}