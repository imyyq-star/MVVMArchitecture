package com.imyyq.mvvm.app

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

/**
 * App 状态监听器，可用于判断应用是在后台还是在前台。
 * 比如引入库：androidx.lifecycle:lifecycle-process，或者是 mvvm-config.gradle 开启 lifecycleProcess
 */
object AppStateTracker {
    private var mIsTract = false
    private var mChangeListener: MutableList<AppStateChangeListener> = mutableListOf()

    /**
     * 前台状态
     */
    const val STATE_FOREGROUND = 0

    /**
     * 后台状态
     */
    const val STATE_BACKGROUND = 1

    /**
     * 获取当前应用的前后台状态，默认是 [STATE_FOREGROUND]，使用此方法必须先调用 [track] 方法
     */
    var currentState = STATE_BACKGROUND
        get() {
            if (!mIsTract) {
                throw RuntimeException("必须先调用 track 方法")
            }
            return field
        }

    /**
     * 监听应用的前后台状态
     */
    fun track(appStateChangeListener: AppStateChangeListener) {
        if (!mIsTract) {
            mIsTract = true
            ProcessLifecycleOwner.get().lifecycle.addObserver(LifecycleChecker())
        }
        mChangeListener.add(appStateChangeListener)
    }

    interface AppStateChangeListener {
        /**
         * 应用进入前台时，回调此方法
         */
        fun appTurnIntoForeground()

        /**
         * 应用进入后台时，回调此方法
         */
        fun appTurnIntoBackground()
    }

    private class LifecycleChecker : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            currentState = STATE_FOREGROUND
            mChangeListener.forEach {
                it.appTurnIntoForeground()
            }
        }

        override fun onPause(owner: LifecycleOwner) {
            currentState = STATE_BACKGROUND
            mChangeListener.forEach {
                it.appTurnIntoBackground()
            }
        }
    }
}