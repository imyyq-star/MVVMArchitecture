package com.imyyq.mvvm.app

import android.app.Activity

/**
 * App 全局 Activity 管理器，采用 registerActivityLifecycleCallbacks 监听所有的 Activity 的创建和销毁。
 * 可通过 [GlobalConfig.gIsNeedActivityManager] 关闭这个功能，默认是关闭的，不开启就使用此类的话，会造成运行时异常。
 */
object AppActivityManager {
    private val mActivityList = mutableListOf<Activity>()

    fun add(activity: Activity) = mActivityList.add(activity)

    fun remove(activity: Activity) = mActivityList.remove(activity)

    /**
     * @return 返回 true，则当前没有打开 activity
     */
    fun isEmpty(): Boolean {
        checkEnabled()
        return mActivityList.isEmpty()
    }

    /**
     * @return 获取指定 activity 对应的对象实例
     */
    fun get(clazz: Class<Activity>): Activity? {
        checkEnabled()
        return mActivityList.find { it.javaClass == clazz }
    }

    /**
     * @return 获取当前的 activity，即最后打开的 activity
     */
    fun currentActivity(): Activity? {
        checkEnabled()
        if (mActivityList.isNotEmpty()) {
            return mActivityList.last()
        }
        return null
    }

    /**
     * 结束当前的 activity，即最后打开的 activity
     */
    fun finishCurrentActivity() {
        checkEnabled()
        currentActivity()?.finish()
    }

    /**
     * 结束指定的 [activity]
     */
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

    /**
     * 结束所有的 Activity，比如在退出应用时使用
     */
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