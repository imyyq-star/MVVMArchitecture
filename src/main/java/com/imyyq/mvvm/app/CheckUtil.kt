package com.imyyq.mvvm.app

import com.imyyq.mvvm.R

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
}