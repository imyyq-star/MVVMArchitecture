package com.imyyq.mvvm.base

import android.app.Dialog
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.imyyq.mvvm.app.GlobalConfig

/**
 * 加载中对话框接口
 */
interface ILoadingDialog {
    /**
     * 显示加载中对话框
     */
    fun showLoadingDialog(dialog: Dialog, msg: String?) {
        dialog.setCancelable(isLoadingDialogCancelable())
        dialog.setCanceledOnTouchOutside(isLoadingDialogCanceledOnTouchOutside())
        dialog.show()
        // 只有在允许取消对话框和同时允许取消任务时，才有必要设置监听
        if (isCancelConsumingTaskWhenLoadingDialogCanceled() &&
            (isLoadingDialogCancelable() || isLoadingDialogCanceledOnTouchOutside())
        ) {
            dialog.setOnCancelListener { onCancelLoadingDialog() }
        }
        dialog.findViewById<TextView>(loadingDialogLayoutMsgId())?.text = msg
    }

    /**
     * 加载中对话框被用户手动取消了，则回调此方法
     */
    fun onCancelLoadingDialog()

    /**
     * 隐藏加载中对话框
     */
    fun dismissLoadingDialog(dialog: Dialog) = dialog.dismiss()

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.gLoadingDialogLayout]
     */
    @LayoutRes
    fun loadingDialogLayout() = GlobalConfig.gLoadingDialogLayout

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.gLoadingDialogLayoutMsgId]
     */
    @IdRes
    fun loadingDialogLayoutMsgId() = GlobalConfig.gLoadingDialogLayoutMsgId

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.gIsNeedLoadingDialog]
     */
    fun isNeedLoadingDialog() = GlobalConfig.gIsNeedLoadingDialog

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.gLoadingDialogCancelable]
     */
    fun isLoadingDialogCancelable() = GlobalConfig.gLoadingDialogCancelable

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.gLoadingDialogCanceledOnTouchOutside]
     */
    fun isLoadingDialogCanceledOnTouchOutside() = GlobalConfig.gLoadingDialogCanceledOnTouchOutside

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.gIsCancelConsumingTaskWhenLoadingDialogCanceled]
     */
    fun isCancelConsumingTaskWhenLoadingDialogCanceled() = GlobalConfig.gIsCancelConsumingTaskWhenLoadingDialogCanceled
}