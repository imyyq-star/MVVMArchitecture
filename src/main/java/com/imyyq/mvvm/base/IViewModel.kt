package com.imyyq.mvvm.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 * ViewModel 层，让 vm 可以感知 v 的生命周期
 */
interface IViewModel : LifecycleObserver {
    /**
     * 当关联的页面生命周期改变时回调
     *
     * @param owner 产生生命周期的源头，比如某个 Activity
     * @param event 生命周期
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAny(owner: LifecycleOwner?, event: Lifecycle.Event?)

    /**
     * 当关联的页面 onCreate 时回调
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate()

    /**
     * 当关联的页面 onDestroy 时回调
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy()

    /**
     * 当关联的页面  时回调
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart()

    /**
     * 当关联的页面 onStop 时回调
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop()

    /**
     * 当关联的页面 onResume 时回调
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume()

    /**
     * 当关联的页面 onPause 时回调
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause()
}