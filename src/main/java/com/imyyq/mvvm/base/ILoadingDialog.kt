package com.imyyq.mvvm.base

import android.app.Dialog
import android.widget.TextView
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
        dialog.findViewById<TextView>(loadingDialogLayoutMsgId())?.text = msg
    }

    /**
     * 隐藏加载中对话框
     */
    fun dismissLoadingDialog(dialog: Dialog) = dialog.dismiss()

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.loadingDialogLayout]
     */
    fun loadingDialogLayout() = GlobalConfig.loadingDialogLayout

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.loadingDialogLayoutMsgId]
     */
    fun loadingDialogLayoutMsgId() = GlobalConfig.loadingDialogLayoutMsgId

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.isNeedLoadingDialog]
     */
    fun isNeedLoadingDialog() = GlobalConfig.isNeedLoadingDialog

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.loadingDialogCancelable]
     */
    fun isLoadingDialogCancelable(): Boolean = GlobalConfig.loadingDialogCancelable

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig.loadingDialogCanceledOnTouchOutside]
     */
    fun isLoadingDialogCanceledOnTouchOutside(): Boolean = GlobalConfig.loadingDialogCanceledOnTouchOutside
}