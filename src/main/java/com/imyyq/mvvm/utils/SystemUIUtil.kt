package com.imyyq.mvvm.utils

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.RequiresApi

object SystemUIUtil {
    /**
     * 开启状态栏的低分辨率模式，部分图标会模糊或隐藏
     */
    fun lowProfile(window: Window, isShow: Boolean = false) {
        if (isShow) {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility.removeFlag(View.SYSTEM_UI_FLAG_LOW_PROFILE)
            return
        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
    }

    /**
     * 隐藏状态栏，只有在手动滑出状态栏时，状态栏才会显示并保持显示
     */
    fun hideStatusBar(window: Window, isShow: Boolean = false) {
        if (isShow) {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility.removeFlag(View.SYSTEM_UI_FLAG_FULLSCREEN)
            return
        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    /**
     * 隐藏状态栏，只有在手动滑出状态栏时，状态栏以半透明的形式显示，并且不会挤压视图，并在一定时间后自动消失
     */
    fun hideStatusBarImmersiveSticky(window: Window, isShow: Boolean = false) {
        if (isShow) {
            val visibility =
                window.decorView.systemUiVisibility.removeFlag(View.SYSTEM_UI_FLAG_FULLSCREEN)
            window.decorView.systemUiVisibility =
                visibility.removeFlag(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            return
        }
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    /**
     * 隐藏导航栏，只有在点击屏幕任意位置时，导航栏才会显示出来，并保持显示
     */
    fun hideNavBar(window: Window, isShow: Boolean = false) {
        if (isShow) {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility.removeFlag(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            return
        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    /**
     * 隐藏导航栏，只有在屏幕底部往上滑出导航栏时才会显示出来，并保持显示
     */
    fun hideNavBarImmersive(window: Window, isShow: Boolean = false) {
        if (isShow) {
            val visibility =
                window.decorView.systemUiVisibility.removeFlag(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            window.decorView.systemUiVisibility = visibility.removeFlag(View.SYSTEM_UI_FLAG_IMMERSIVE)
            return
        }

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
    }

    /**
     * 隐藏导航栏，只有在屏幕底部往上滑出导航栏时才会显示出来，并保持半透明显示，一定时间后消失
     */
    fun hideNavBarImmersiveStick(window: Window, isShow: Boolean = false) {
        if (isShow) {
            val visibility =
                window.decorView.systemUiVisibility.removeFlag(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            window.decorView.systemUiVisibility =
                visibility.removeFlag(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            return
        }

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    /**
     * 隐藏导航栏和状态栏
     */
    fun hideStatusNavBarImmersiveStick(window: Window, isShow: Boolean = false) {
        if (isShow) {
            var visibility =
                window.decorView.systemUiVisibility.removeFlag(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            visibility =
                visibility.removeFlag(View.SYSTEM_UI_FLAG_FULLSCREEN)
            window.decorView.systemUiVisibility =
                visibility.removeFlag(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            return
        }

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    /**
     * 隐藏导航栏和状态栏
     */
    fun hideStatusNavBar(window: Window, isShow: Boolean = false) {
        if (isShow) {
            var visibility =
                window.decorView.systemUiVisibility.removeFlag(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            visibility = visibility.removeFlag(View.SYSTEM_UI_FLAG_FULLSCREEN)
            window.decorView.systemUiVisibility = visibility
            return
        }

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
    }


    /**
     * 把状态栏的图标设置为深色的
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun dartStatusBarIcon(window: Window, isLight: Boolean = false) {
        if (isLight) {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility.removeFlag(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            return
        }

        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    /**
     * 把导航栏的图标设置为深色的
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun dartNavBarIcon(window: Window, isLight: Boolean = false) {
        if (isLight) {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility.removeFlag(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
            return
        }

        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }

    /**
     * 让布局稳定，不受 SystemUI 影响
     */
    fun stableLayout(window: Window, unstable: Boolean = false) {
        if (unstable) {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility.removeFlag(View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            return
        }

        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    /**
     * 让布局延伸到导航栏和状态栏之下，并让导航栏和状态栏的背景颜色为透明。
     * 需要设置根布局的 fitsSystemWindows 为 true，这里默认自动寻找根布局并设置
     */
    fun fullscreenStable(window: Window) {
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        val content = window.decorView.findViewById<ViewGroup>(android.R.id.content)
        content.getChildAt(0).fitsSystemWindows = true
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    /**
     * 沉浸式状态，手动拉出导航栏或状态栏时，背景是半透明的，且会自动消失
     */
    fun fullscreenImmersive(window: Window) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
    }

}