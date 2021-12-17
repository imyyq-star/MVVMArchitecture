package com.imyyq.mvvm.base

import android.content.Intent

/**
 * 封装 androidx ActivityResult API 的回调接口，让回调在界面和 vm 中都可以收到。
 * 只有使用 V、View 中 start 开头的方法，此接口才保证有效.
 *
 * 通常一个 activity 只有一个 onActivityResult requestCode 的请求，因此此接口只封装了一个，接口屏蔽了 requestCode 和 resultCode。
 * 但是也有多个 requestCode 的可能，如果有多个的需求，建议使用 androidx 的 ActivityResult API。
 * 不要使用原生的 startActivityForResult。
 *
 * @author imyyq.star@gmail.com
 */
interface IActivityResult {
    /**
     * Activity.RESULT_OK
     */
    fun onActivityResultOk(): ((data: Intent?) -> Unit)? = null

    /**
     * Activity.RESULT_CANCELED
     */
    fun onActivityResultCanceled(): ((data: Intent?) -> Unit)? = null
}