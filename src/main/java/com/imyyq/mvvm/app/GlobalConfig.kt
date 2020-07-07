package com.imyyq.mvvm.app

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.imyyq.mvvm.R
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir


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
    var loadingDialogLayout = R.layout.mvvm_dlg_loading

    /**
     * 加载中对话框的 layout 消息id
     */
    @IdRes
    var loadingDialogLayoutMsgId = R.id.tv_msg

    /**
     * activity 是否支持侧滑返回。
     * 若支持，则 theme 必须实现以下属性：
     * <item name="android:windowIsTranslucent">true</item>
     *
     * 并且框架会自动把 windowBackground 设置为透明
     */
    var isSupportSwipe = false

    /**
     * ViewModel 是否可以调用 finish 和 startActivity 方法
     */
    var isViewModelNeedStartAndFinish = false

    /**
     * ViewModel 是否可以调用 startActivityForResult 方法
     */
    var isViewModelNeedStartForResult = false

    /**
     * 在 xml 配置点击事件，可配置的属性如下：
     * onClickCommand 点击事件
     * isInterval 是否开启防止点击过快
     * intervalMilliseconds 防止点击过快的间隔时间，毫秒为单位
     *
     * 这里可全局设置是否开启防止点击事件过快的功能，局部可单独开启或关闭。
     *
     * 如果关闭，那么和 setOnClickListener 没啥区别
     */
    var mIsClickInterval: Boolean = false

    /**
     * 点击事件时间间隔
     */
    var mClickIntervalMilliseconds = 800

    /**
     * 是否需要管理 Activity 堆栈
     */
    var mIsNeedActivityManager = true

    /**
     * 初始化 LoadSir 的相关界面。
     * [defCallback] 默认的界面，通常是加载中页面，设置了后，默认打开开启了 LoadSir 的页面后就显示这里设置的页面。
     * [clazz] 其他的状态页，比如空页面，加载错误等。
     */
    fun initLoadSir(defCallback: Class<*>, vararg clazz: Class<*>) {
        val builder = LoadSir.beginBuilder()
        clazz.forEach {
            builder.addCallback(it.newInstance() as Callback)
        }
        builder.addCallback(defCallback.newInstance() as Callback)
        //设置默认状态页
        builder.setDefaultCallback(defCallback as Class<out Callback>)
            .commit()
    }
}