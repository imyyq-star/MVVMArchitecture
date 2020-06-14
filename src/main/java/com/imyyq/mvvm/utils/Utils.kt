package com.imyyq.mvvm.utils

import android.os.Build
import com.imyyq.mvvm.app.BaseApp

object Utils {
    val isNeedCheckPermission: Boolean
        get() = Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    fun dp2px(dpValue: Float): Int {
        val scale: Float = BaseApp.getInstance().getResources().getDisplayMetrics().density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * px转dp
     *
     * @param pxValue px值
     * @return dp值
     */
    fun px2dp(pxValue: Float): Int {
        val scale: Float = BaseApp.getInstance().getResources().getDisplayMetrics().density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * sp转px
     *
     * @param spValue sp值
     * @return px值
     */
    fun sp2px(spValue: Float): Int {
        val fontScale: Float =
            BaseApp.getInstance().getResources().getDisplayMetrics().scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * px转sp
     *
     * @param pxValue px值
     * @return sp值
     */
    fun px2sp(pxValue: Float): Int {
        val fontScale: Float =
            BaseApp.getInstance().getResources().getDisplayMetrics().scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }
}