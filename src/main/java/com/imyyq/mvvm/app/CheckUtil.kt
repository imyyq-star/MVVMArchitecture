package com.imyyq.mvvm.app

import androidx.lifecycle.LiveData
import com.imyyq.mvvm.R

object CheckUtil {

    fun checkStartAndFinishEvent(event: LiveData<*>?) {
        if (event == null) {
            throw RuntimeException(
                BaseApp.getInstance().getString(R.string.start_activity_finish_tips)
            )
        }
    }

    fun checkStartForResultEvent(event: LiveData<*>?) {
        if (event == null) {
            throw RuntimeException(
                BaseApp.getInstance().getString(R.string.start_activity_for_result_tips)
            )
        }
    }

    fun checkLoadSirEvent(event: LiveData<*>?) {
        if (event == null) {
            throw RuntimeException(BaseApp.getInstance().getString(R.string.load_sir_tips))
        }
    }

    fun checkLoadingDialogEvent(event: LiveData<*>?) {
        if (event == null) {
            throw RuntimeException(BaseApp.getInstance().getString(R.string.loadingDialogTips))
        }
    }
}