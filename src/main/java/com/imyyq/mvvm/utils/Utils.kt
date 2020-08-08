package com.imyyq.mvvm.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.imyyq.mvvm.R
import java.io.Serializable

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

    fun getIntentByMapOrBundle(
        context: Context?,
        clz: Class<out Activity>?,
        map: Map<String, *>? = null,
        bundle: Bundle? = null
    ): Intent {
        val intent = Intent(context, clz)
        map?.forEach { entry ->
            @Suppress("UNCHECKED_CAST")
            when (val value = entry.value) {
                is Boolean -> {
                    intent.putExtra(entry.key, value)
                }
                is BooleanArray -> {
                    intent.putExtra(entry.key, value)
                }
                is Byte -> {
                    intent.putExtra(entry.key, value)
                }
                is ByteArray -> {
                    intent.putExtra(entry.key, value)
                }
                is Char -> {
                    intent.putExtra(entry.key, value)
                }
                is CharArray -> {
                    intent.putExtra(entry.key, value)
                }
                is Short -> {
                    intent.putExtra(entry.key, value)
                }
                is ShortArray -> {
                    intent.putExtra(entry.key, value)
                }
                is Int -> {
                    intent.putExtra(entry.key, value)
                }
                is IntArray -> {
                    intent.putExtra(entry.key, value)
                }
                is Long -> {
                    intent.putExtra(entry.key, value)
                }
                is LongArray -> {
                    intent.putExtra(entry.key, value)
                }
                is Float -> {
                    intent.putExtra(entry.key, value)
                }
                is FloatArray -> {
                    intent.putExtra(entry.key, value)
                }
                is Double -> {
                    intent.putExtra(entry.key, value)
                }
                is DoubleArray -> {
                    intent.putExtra(entry.key, value)
                }
                is String -> {
                    intent.putExtra(entry.key, value)
                }
                is CharSequence -> {
                    intent.putExtra(entry.key, value)
                }
                is Parcelable -> {
                    intent.putExtra(entry.key, value)
                }
                is Serializable -> {
                    intent.putExtra(entry.key, value)
                }
                is Bundle -> {
                    intent.putExtra(entry.key, value)
                }
                is Intent -> {
                    intent.putExtra(entry.key, value)
                }
                is ArrayList<*> -> {
                    val any = if (value.isNotEmpty()) {
                        value[0]
                    } else null
                    if (any is String) {
                        intent.putExtra(entry.key, value as ArrayList<String>)
                    } else if (any is Parcelable) {
                        intent.putExtra(entry.key, value as ArrayList<Parcelable>)
                    } else if (any is Int) {
                        intent.putExtra(entry.key, value as ArrayList<Int>)
                    } else if (any is CharSequence) {
                        intent.putExtra(entry.key, value as ArrayList<CharSequence>)
                    } else {
                        throw RuntimeException("不支持此类型 $value")
                    }
                }
                is Array<*> -> {
                    if (value.isArrayOf<String>()) {
                        intent.putExtra(entry.key, value as Array<String>)
                    } else if (value.isArrayOf<Parcelable>()) {
                        intent.putExtra(entry.key, value as Array<Parcelable>)
                    } else if (value.isArrayOf<CharSequence>()) {
                        intent.putExtra(entry.key, value as Array<CharSequence>)
                    } else {
                        throw RuntimeException("不支持此类型 $value")
                    }
                }
                else -> {
                    throw RuntimeException("不支持此类型 $value")
                }
            }
        }
        bundle?.let { intent.putExtras(bundle) }
        return intent
    }
}