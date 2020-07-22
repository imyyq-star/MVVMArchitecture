package com.imyyq.mvvm.base

import android.content.Intent

/**
 * 封装 ActivityResult 的回调接口，让回调在界面和 vm 中都可以收到
 */
interface IActivityResult {
    fun onActivityResultOk(intent: Intent) {}

    fun onActivityResult(resultCode: Int, intent: Intent) {}

    fun onActivityResultCanceled(intent: Intent) {}
}