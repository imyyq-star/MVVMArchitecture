package com.imyyq.mvvm.base.view

import com.kingja.loadsir.callback.Callback

/**
 * 加载中接口
 *
 * @author imyyq.star@gmail.com
 */
interface ILoading {
    fun initLoadSir()

    /**
     * 加载失败，显示 LoadSir 的页面
     */
    fun onLoadSirShowed(it: Class<out Callback>) {}

    /**
     * 加载成功，LoadSir 消失，显示结果页
     */
    fun onLoadSirSuccess() {}

    /**
     * 如果出现错误页等需要重新加载的页面，可在此方法中接收回调
     */
    fun onLoadSirReload() {}

    /**
     * 获取 LoadSir 的目标，通常是 Activity，或者是某个 view，LoadSir 的页面会挂在该 view 上
     */
    fun getLoadSirTarget(): Any? = null
}