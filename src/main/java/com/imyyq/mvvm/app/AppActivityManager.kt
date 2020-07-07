package com.imyyq.mvvm.app

import android.app.Activity

/**
 * App 全局 Activity 管理器，采用 registerActivityLifecycleCallbacks 监听所有的 Activity 的创建和销毁。
 * 可通过 [GlobalConfig.gIsNeedActivityManager] 关闭这个功能
 */
object AppActivityManager {
    private val mActivityList = mutableListOf<Activity>()

    fun add(activity: Activity) = mActivityList.add(activity)

    fun remove(activity: Activity) = mActivityList.remove(activity)

    fun isEmpty(): Boolean {
        checkEnabled()
        return mActivityList.isEmpty()
    }

    fun get(clazz: Class<Activity>): Activity? {
        checkEnabled()
        return mActivityList.find { it.javaClass == clazz }
    }

    fun current(): Activity? {
        checkEnabled()
        if (mActivityList.isNotEmpty()) {
            return mActivityList.last()
        }
        return null
    }

    fun finishCurrentActivity() {
        checkEnabled()
        current()?.finish()
    }

    fun finishActivity(activity: Activity) {
        checkEnabled()
        mActivityList.forEach {
            if (it == activity) {
                remove(activity)
                it.finish()
                return
            }
        }
    }

    fun finishAllActivity() {
        checkEnabled()
        mActivityList.forEach {
            it.finish()
        }
        mActivityList.clear()
    }

    private fun checkEnabled() {
        if (!GlobalConfig.gIsNeedActivityManager) {
            throw RuntimeException("GlobalConfig.mIsNeedActivityManager 开关没有打开，不能使用 AppActivityManager 类")
        }
    }
}