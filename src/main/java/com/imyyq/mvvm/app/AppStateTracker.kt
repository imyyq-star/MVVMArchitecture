package com.imyyq.mvvm.app

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

/**
 * App 状态监听器，可用于判断应用是在后台还是在前台
 */
object AppStateTracker {
    private var mIsTract = false
    private var mChangeListener: MutableList<AppStateChangeListener> = mutableListOf()
    const val STATE_FOREGROUND = 0
    const val STATE_BACKGROUND = 1
    var currentState = STATE_BACKGROUND
        get() {
            if (!mIsTract) {
                throw RuntimeException("必须先调用 track 方法")
            }
            return field
        }

    fun track(appStateChangeListener: AppStateChangeListener) {
        if (!mIsTract) {
            mIsTract = true
            ProcessLifecycleOwner.get().lifecycle.addObserver(LifecycleChecker())
        }
        mChangeListener.add(appStateChangeListener)
    }

    interface AppStateChangeListener {
        fun appTurnIntoForeground()
        fun appTurnIntoBackground()
    }

    class LifecycleChecker : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            currentState = STATE_FOREGROUND
            mChangeListener.forEach {
                it.appTurnIntoForeground()
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            currentState = STATE_BACKGROUND
            mChangeListener.forEach {
                it.appTurnIntoBackground()
            }
        }
    }
}