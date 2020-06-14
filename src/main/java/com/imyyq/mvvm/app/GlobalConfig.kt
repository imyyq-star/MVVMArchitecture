package com.imyyq.mvvm.app

import androidx.annotation.LayoutRes
import com.imyyq.mvvm.R

/**
 * 全局配置
 */
object GlobalConfig {
    /**
     * 加载中对话框是否可点击返回键隐藏对话框
     */
    var loadingDialogCancelable = false

    /**
     * 加载中对话框是否可点击对话框内容外的区域隐藏对话框
     */
    var loadingDialogCanceledOnTouchOutside = false

    /**
     * 页面在显示数据时需要时间，因此显示加载中对话框，如果不需要，可以设置为 false，以免创建不必要的对象
     */
    var isNeedLoadingDialog = true

    /**
     * 加载中对话框的 layout
     */
    @LayoutRes
    var loadingDialogLayout = R.layout.dlg_loading
}