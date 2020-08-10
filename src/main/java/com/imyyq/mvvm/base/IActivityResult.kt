package com.imyyq.mvvm.base

import android.content.Intent

/**
 * 封装 ActivityResult 的回调接口，让回调在界面和 vm 中都可以收到。
 * 只有使用 V、View 中 start 开头的方法，此接口才保证有效
 */
interface IActivityResult {
    /**
     * Activity.RESULT_OK
     */
    fun onActivityResultOk(intent: Intent) {}

    /**
     * 其他状态码
     */
    fun onActivityResult(resultCode: Int, intent: Intent) {}

    /**
     * Activity.RESULT_CANCELED
     */
    fun onActivityResultCanceled(intent: Intent) {}
}