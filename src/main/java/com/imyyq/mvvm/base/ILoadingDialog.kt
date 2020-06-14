package com.imyyq.mvvm.base

import com.imyyq.mvvm.R

/**
 * 对话框接口
 */
interface ILoadingDialog {
    fun showLoadingDialog(msg: String?)

    fun dismissLoadingDialog()

    fun loadingDialogLayout() = R.layout.dlg_loading

    /**
     * 页面在显示数据时需要时间，因此显示对话框，如果不需要，可以设置为 false，以免创建不必要的对象
     */
    fun isNeedLoadingDialog() = true

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig]
     */
    fun isLoadingDialogCancelable(): Boolean? = null

    /**
     * 详见 [com.imyyq.mvvm.app.GlobalConfig]
     */
    fun isLoadingDialogCanceledOnTouchOutside(): Boolean? = null
}