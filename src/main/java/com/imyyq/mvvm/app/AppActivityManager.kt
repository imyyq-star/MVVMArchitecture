package com.imyyq.mvvm.app

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * App 全局 Activity 管理器，采用 registerActivityLifecycleCallbacks 监听所有的 Activity 的创建和销毁。
 * 可通过 [GlobalConfig.gIsNeedActivityManager] 关闭这个功能，默认是关闭的，不开启就使用此类的话，会造成运行时异常。
 *
 * @author imyyq.star@gmail.com
 */
object AppActivityManager {
    private val mActivityList = mutableListOf<Activity>()

    internal fun init() {
        BaseApp.getInstance().registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                remove(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityCreated(
                activity: Activity,
                savedInstanceState: Bundle?
            ) {
                add(activity)
            }

            override fun onActivityResumed(activity: Activity) {
            }
        })
    }

    /**
     * 添加 [activity]
     */
    fun add(activity: Activity) = mActivityList.add(activity)

    /**
     * 移除 [activity]
     */
    fun remove(activity: Activity) = mActivityList.remove(activity)

    /**
     * @return 返回 true，则当前没有打开 activity
     */
    fun isEmpty(): Boolean {
        CheckUtil.checkActivityManager()
        return mActivityList.isEmpty()
    }

    /**
     * @return 获取指定 activity 对应的对象实例
     */
    fun getActivity(clazz: Class<Activity>): Activity? {
        CheckUtil.checkActivityManager()
        return mActivityList.find { it.javaClass == clazz }
    }

    fun getActivities(): List<Activity> = mActivityList

    /**
     * @return 获取当前的 activity，即最后打开的 activity
     */
    fun currentActivity(): Activity? {
        CheckUtil.checkActivityManager()
        if (mActivityList.isNotEmpty()) {
            return mActivityList.last()
        }
        return null
    }

    /**
     * 结束当前的 activity，即最后打开的 activity
     */
    fun finishCurrentActivity() {
        CheckUtil.checkActivityManager()
        currentActivity()?.finish()
    }

    /**
     * 结束指定的 [activity]
     */
    fun finishActivity(activity: Activity) {
        CheckUtil.checkActivityManager()
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
        CheckUtil.checkActivityManager()
        mActivityList.forEach {
            it.finish()
        }
        mActivityList.clear()
    }
}