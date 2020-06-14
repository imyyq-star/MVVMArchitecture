package com.imyyq.mvvm.utils

import android.util.Log

/**
 * Created by 杨永青 on 16/9/13.
 * 可以通过AS的 File | Settings | Editor | General | Auto Import - Exclude from import and completion来禁用掉系统的Log类
 * 再通过 File | Settings | Editor | Live Templates，来设置快捷生成代码，可方便的访问此类
 */
object LogUtil {
    /**
     * 是否强制输出log，默认是Release不输出log，可在Release的时候，提供一个隐藏入口，比如点击某个地方10次打开log输出
     * 好处是可以在Release版本看到log，可放心的在各个地方打log
     */
    var mIsForceLog = false

    // 不是release，没有强制输出log，就不输出
    private const val mIsLog: Boolean = !Utils.isRelease

    fun init() {
        CrashHandlerUtil.init()
    }

    fun i(
        tag: String,
        msg: String
    ) {
        // 不是release，没有强制输出log，就不输出
        if (mIsLog || mIsForceLog) {
            Log.i(tag, msg)
        }
    }

    // Release异常会被传上Bugly
    fun e(
        tag: String,
        msg: String
    ) {
        if (mIsLog || mIsForceLog) {
            Log.e(tag, msg)
        }
    }

    /*
     * 自定义的Log输出，方便一些框架型的log，集中通过前缀tag来过滤==============================================
     */

    fun commonLog(
        tag: String,
        msg: String
    ) {
        i("commonLog - $tag", msg)
    }
}
