package com.imyyq.mvvm.utils

import android.app.Activity
import android.content.ContextWrapper
import android.view.View
import com.imyyq.mvvm.R

/**
 * 测量 view 的大小
 *
 * @return 返回 int 数组，包含宽高
 */
fun View.measureView(): IntArray {
    val arr = IntArray(2)
    val spec = View.MeasureSpec.makeMeasureSpec(
        View.MeasureSpec.UNSPECIFIED,
        View.MeasureSpec.UNSPECIFIED
    )
    measure(spec, spec)
    arr[0] = measuredWidth
    arr[1] = measuredHeight
    return arr
}

/**
 * 连续点击达到点击次数后回调监听
 */
fun View.multiClickListener(frequency: Int, listener: (() -> Unit)? = null) {
    val startIndex = 1
    val interval = 400
    setTag(R.id.multiClickFrequency, startIndex)
    setTag(R.id.multiClickLastTime, System.currentTimeMillis())

    setOnClickListener {
        var f = getTag(R.id.multiClickFrequency) as Int

        // 点击间隔超时，重置次数
        if (System.currentTimeMillis() - (getTag(R.id.multiClickLastTime) as Long) > interval) {
            setTag(R.id.multiClickFrequency, startIndex)
            f = startIndex
        }

        // 第一次点击，重置时间
        if (f == startIndex) {
            setTag(R.id.multiClickLastTime, System.currentTimeMillis())
        }

        if (f == frequency) {
            setTag(R.id.multiClickFrequency, startIndex)

            listener?.invoke()
        } else {
            val lastTime = getTag(R.id.multiClickLastTime) as Long
            if (System.currentTimeMillis() - lastTime < interval) {
                setTag(R.id.multiClickFrequency, f + 1)
            }
            setTag(R.id.multiClickLastTime, System.currentTimeMillis())
        }
    }
}

fun View.getActivity(): Activity? {
    var contextTemp = context
    if (contextTemp is Activity) return contextTemp
    while (contextTemp is ContextWrapper) {
        if (contextTemp is Activity) {
            return contextTemp
        }
        contextTemp = contextTemp.baseContext
    }
    return null
}