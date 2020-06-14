package com.imyyq.mvvm.utils

import android.os.Build
import android.view.View
import com.imyyq.mvvm.BuildConfig

object Utils {
    const val isRelease: Boolean = "release" == BuildConfig.BUILD_TYPE

    val isNeedCheckPermission: Boolean
        get() = Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1

    /**
     * 测量 view 的大小，返回宽高
     */
    fun measureView(view: View): IntArray {
        val arr = IntArray(2)
        val spec = View.MeasureSpec.makeMeasureSpec(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        view.measure(spec, spec)
        arr[0] = view.measuredWidth
        arr[1] = view.measuredHeight
        return arr
    }

    /**
     * 判断集合是否为空
     */
    fun <E> isEmpty(e: Collection<E>?) = e == null || e.isEmpty()
}