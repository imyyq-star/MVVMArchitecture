package com.imyyq.mvvm.app

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.imyyq.mvvm.base.BaseStateManager

/**
 * App 状态监听器，可用于判断应用是在后台还是在前台。
 * 比如引入库：androidx.lifecycle:lifecycle-process，或者是 mvvm-config.gradle 开启 lifecycleProcess
 *
 * @author imyyq.star@gmail.com
 */
object AppStateManager : BaseStateManager<AppStateManager.AppStateListener>() {
    private var mState: Lifecycle.State = Lifecycle.State.RESUMED
    private var mIsInit = true

    init {
        try {
            ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver())
        } catch (e: Throwable) {
            e.printStackTrace()

            CheckUtil.checkAppStateManager()
        }
    }

    override fun onAddListener(listener: AppStateListener) {
        if (mState == Lifecycle.State.RESUMED) {
            listener.onForeground()
        } else {
            listener.onBackground()
        }
    }

    private class AppLifecycleObserver : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            if (!mIsInit) {
                forEach { it.onForeground() }
            }
            mIsInit = false
            mState = Lifecycle.State.RESUMED
        }

        override fun onPause(owner: LifecycleOwner) {
            forEach { it.onBackground() }
            mState = Lifecycle.State.STARTED
        }
    }

    interface AppStateListener {
        /**
         * 应用进入前台时，回调此方法
         */
        fun onForeground()

        /**
         * 应用进入后台时，回调此方法
         */
        fun onBackground()
    }
}