package com.imyyq.mvvm.app

import android.app.Activity

/**
 * App 全局 Activity 管理器，采用 registerActivityLifecycleCallbacks 监听所有的 Activity 的创建和销毁
 */
object AppActivityManager {
    private val mActivityList = mutableListOf<Activity>()

    fun add(activity: Activity) {
        mActivityList.add(activity)
    }

    fun remove(activity: Activity) {
        if (mActivityList.isNotEmpty()) {
            mActivityList.remove(activity)
        }
    }

    fun isEmpty(): Boolean {
        return mActivityList.isEmpty()
    }

    fun get(clazz: Class<Activity>): Activity? {
        return mActivityList.find {
            it.javaClass == clazz
        }
    }

    fun current(): Activity? {
        if (mActivityList.isNotEmpty()) {
            return mActivityList[mActivityList.size - 1]
        }
        return null
    }

    fun finishCurrentActivity() {
        current()?.finish()
    }

    fun finishActivity(activity: Activity) {
        mActivityList.forEach {
            if (it == activity) {
                remove(activity)
                it.finish()
                return
            }
        }
    }

    fun finishAllActivity() {
        mActivityList.forEach {
            it.finish()
        }
        mActivityList.clear()
    }
}