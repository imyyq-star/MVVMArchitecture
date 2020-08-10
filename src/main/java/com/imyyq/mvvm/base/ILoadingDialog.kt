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
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.LoadingDialog.gLoadingDialogLayout]
     */
    @LayoutRes
    fun loadingDialogLayout() = GlobalConfig.LoadingDialog.gLoadingDialogLayout

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.LoadingDialog.gLoadingDialogLayoutMsgId]
     */
    @IdRes
    fun loadingDialogLayoutMsgId() = GlobalConfig.LoadingDialog.gLoadingDialogLayoutMsgId

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.LoadingDialog.gIsNeedLoadingDialog]
     */
    fun isNeedLoadingDialog() = GlobalConfig.LoadingDialog.gIsNeedLoadingDialog

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.LoadingDialog.gLoadingDialogCancelable]
     */
    fun isLoadingDialogCancelable() = GlobalConfig.LoadingDialog.gLoadingDialogCancelable

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.LoadingDialog.gLoadingDialogCanceledOnTouchOutside]
     */
    fun isLoadingDialogCanceledOnTouchOutside() = GlobalConfig.LoadingDialog.gLoadingDialogCanceledOnTouchOutside

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.LoadingDialog.gIsCancelConsumingTaskWhenLoadingDialogCanceled]
     */
    fun isCancelConsumingTaskWhenLoadingDialogCanceled() = GlobalConfig.LoadingDialog.gIsCancelConsumingTaskWhenLoadingDialogCanceled
}