package com.imyyq.mvvm.utils

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.imyyq.mvvm.app.BaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 吐司提示类，可设置位置，偏移量，默认是系统自带的位置和偏移量。
 * 可设置自定义布局和消息id。
 */
object ToastUtil {

    private var mCustomLayout = -1
    private var mCustomMsgId = -1
    private var mGravity = 0
    private var xOffset = 0
    private var yOffset = 0

    init {
        GlobalScope.launch(Dispatchers.Main) {
            @SuppressLint("ShowToast")
            val toast = Toast.makeText(BaseApp.getInstance(), "", Toast.LENGTH_SHORT)
            mGravity = toast.gravity
            xOffset = toast.xOffset
            yOffset = toast.yOffset
        }
    }

    /**
     * 系统自带的短消息提示
     *
     * @param stringResID 消息内容
     */
    fun showShortToast(
            @StringRes
            stringResID: Int) {
        showToast(stringResID, Toast.LENGTH_SHORT)
    }

    /**
     * 系统自带的短消息提示
     *
     * @param msg 消息内容
     */
    fun showShortToast(msg: String) {
        showToast(msg, Toast.LENGTH_SHORT)
    }

    /**
     * 系统自带的长消息提示
     *
     * @param stringResID 消息内容
     */
    fun showLongToast(
            @StringRes
            stringResID: Int) {
        showToast(stringResID, Toast.LENGTH_LONG)
    }

    /**
     * 系统自带的长消息提示
     *
     * @param msg 消息内容
     */
    fun showLongToast(msg: String) {
        showToast(msg, Toast.LENGTH_LONG)
    }

    private fun showToast(
        @StringRes
        stringResID: Int, duration: Int
    ) {
        showToast(BaseApp.getInstance().getString(stringResID), duration)
    }

    private fun showToast(msg: String, duration: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            val toast = Toast.makeText(BaseApp.getInstance(), msg, duration)
            toast.setGravity(mGravity, xOffset, yOffset)
            toast.show()
        }
    }

    // 设定 Toast 的相关参数

    fun setCustomLayout(mCustomLayout: Int) {
        ToastUtil.mCustomLayout = mCustomLayout
    }

    fun setCustomMsgId(mCustomMsgId: Int) {
        ToastUtil.mCustomMsgId = mCustomMsgId
    }

    fun setGravity(mGravity: Int) {
        ToastUtil.mGravity = mGravity
    }

    fun setYOffset(yOffset: Int) {
        ToastUtil.yOffset = yOffset
    }

    fun setXOffset(xOffset: Int) {
        ToastUtil.xOffset = xOffset
    }

    // 以下是显示自定义的 Toast ============================================

    fun showCustomLongToast(msg: String) {
        showCustomToast(msg, Toast.LENGTH_LONG)
    }


    fun showCustomToast(msg: String, length: Int) {
        if (mCustomLayout == -1 || mCustomMsgId == -1) {
            throw RuntimeException("必须初始化mCustomLayout和mCustomMsgId")
        }
        showCustomToast(
            msg, mCustomLayout, mCustomMsgId, length,
            mGravity, xOffset, yOffset
        )
    }

    fun showCustomShortToast(msgRes: Int) {
        showCustomToast(BaseApp.getInstance().getString(msgRes), Toast.LENGTH_SHORT)
    }

    fun showCustomShortToast(msg: String) {
        showCustomToast(msg, Toast.LENGTH_SHORT)
    }

    fun showCustomLongToast(
            @LayoutRes
            layout: Int) {
        showCustomToast(null, layout, 0, Toast.LENGTH_LONG, Gravity.CENTER, 0, 0)
    }

    fun showCustomLongToast(msg: String,
                            @LayoutRes
                            layout: Int,
                            @IdRes
                            msgId: Int) {
        showCustomToast(msg, layout, msgId, Toast.LENGTH_LONG, Gravity.CENTER, 0, 0)
    }

    fun showCustomToast(msg: String?,
                        @LayoutRes
                        layout: Int,
                        @IdRes
                        msgId: Int, duration: Int, gravity: Int, xOffset: Int,
                        yOffset: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            val toast = Toast(BaseApp.getInstance())
            val view = LayoutInflater.from(BaseApp.getInstance()).inflate(layout, null)
            if (msgId != 0) {
                val tv = view.findViewById<TextView>(msgId)
                tv.text = msg
            }
            toast.view = view
            toast.duration = duration
            toast.setGravity(gravity, xOffset, yOffset)
            toast.show()
        }
    }
}
