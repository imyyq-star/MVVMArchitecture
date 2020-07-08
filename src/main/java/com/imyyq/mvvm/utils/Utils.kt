package com.imyyq.mvvm.utils

import android.os.Build
import android.view.View
import com.imyyq.mvvm.R

object Utils {
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

    /**
     * 连续点击达到点击次数后回调监听
     */
    @JvmStatic
    fun multiClickListener(view: View, frequency: Int, listener: (() -> Unit)? = null) {
        view.setTag(R.id.multiClickFrequency, 0)
        view.setTag(R.id.multiClickLastTime, System.currentTimeMillis())
        view.setOnClickListener {
            val f = view.getTag(R.id.multiClickFrequency) as Int
            if (f == frequency) {
                view.setTag(R.id.multiClickFrequency, 0)
                view.setTag(R.id.multiClickLastTime, System.currentTimeMillis())

                listener?.invoke()
            } else {
                val lastTime = view.getTag(R.id.multiClickLastTime) as Long
                if (System.currentTimeMillis() - lastTime < 400) {
                    view.setTag(R.id.multiClickFrequency, f + 1)
                }
                view.setTag(R.id.multiClickLastTime, System.currentTimeMillis())
            }
        }
    }
}