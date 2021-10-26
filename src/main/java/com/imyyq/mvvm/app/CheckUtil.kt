package com.imyyq.mvvm.app

import com.imyyq.mvvm.R

/**
 * @author imyyq.star@gmail.com
 */
internal object CheckUtil {

    fun checkStartAndFinishEvent(event: Any?) {
        if (event == null) {
            throw RuntimeException(
                BaseApp.getInstance().getString(R.string.start_activity_finish_tips)
            )
        }
    }

    fun checkStartForResultEvent(event: Any?) {
        if (event == null) {
            throw RuntimeException(
                BaseApp.getInstance().getString(R.string.start_activity_for_result_tips)
            )
        }
    }

    fun checkLoadSirEvent(event: Any?) {
        if (event == null) {
            throw RuntimeException(BaseApp.getInstance().getString(R.string.load_sir_tips))
        }
    }

    fun checkLoadingDialogEvent(event: Any?) {
        if (event == null) {
            throw RuntimeException(BaseApp.getInstance().getString(R.string.loadingDialogTips))
        }
    }

    fun checkActivityManager() {
        if (!GlobalConfig.gIsNeedActivityManager) {
            throw RuntimeException("GlobalConfig.mIsNeedActivityManager 开关没有打开，不能使用 ${AppActivityManager.javaClass.simpleName} 类")
        }
    }

    fun checkAppStateManager() {
        throw RuntimeException("mvvm-config.gradle 未开启 lifecycleProcess，不能使用 ${AppStateManager.javaClass.simpleName} 类")
    }
}