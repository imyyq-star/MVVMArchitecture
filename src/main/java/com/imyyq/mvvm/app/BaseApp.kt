package com.imyyq.mvvm.app

import android.app.Activity
import android.app.Application
import android.os.Bundle

open class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        initApp(this)
    }

    companion object {
        private lateinit var app: Application

        @JvmStatic
        fun initApp(app: Application) {
            Companion.app = app

            // 监听所有 Activity 的创建和销毁
            if (GlobalConfig.gIsNeedActivityManager) {
                app.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
                    override fun onActivityPaused(activity: Activity) {
                    }

                    override fun onActivityStarted(activity: Activity) {
                    }

                    override fun onActivityDestroyed(activity: Activity) {
                        AppActivityManager.remove(activity)
                    }

                    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                    }

                    override fun onActivityStopped(activity: Activity) {
                    }

                    override fun onActivityCreated(
                        activity: Activity,
                        savedInstanceState: Bundle?
                    ) {
                        AppActivityManager.add(activity)
                    }

                    override fun onActivityResumed(activity: Activity) {
                    }

                })
            }
        }

        @JvmStatic
        fun getInstance(): Application {
            return app
        }
    }
}