package com.imyyq.mvvm.base

import android.app.Application
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.imyyq.mvvm.app.GlobalConfig
import com.imyyq.mvvm.utils.SingleLiveEvent

open class BaseViewModel<M : BaseModel>(app: Application) : AndroidViewModel(app), IViewModel {
    constructor(app: Application, model: M) : this(app) {
        mModel = model
    }

    // 可能存在没有仓库的 vm，但我们这里也不要是可 null 的
    protected lateinit var mModel: M

    val mUiChangeLiveData: UiChangeLiveData by lazy {
        UiChangeLiveData()
    }

    override fun onAny(owner: LifecycleOwner?, event: Lifecycle.Event?) {
    }

    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    @CallSuper
    override fun onCleared() {
        // 可能 mModel 是未初始化的
        if (this::mModel.isInitialized) {
            mModel.onCleared()
        }
    }

    @MainThread
    protected fun showLoadingDialog() {
        showLoadingDialog("请稍后...")
    }

    @MainThread
    protected fun showLoadingDialog(title: String?) {
        if (!mUiChangeLiveData.showLoadingDialogEvent.hasObservers()) {
            throw RuntimeException("Activity 或 Fragment 复写了 isNeedLoadingDialog() 方法并返回 false 时，无法使用 Dialog")
        }
        mUiChangeLiveData.showLoadingDialogEvent.value = title
    }

    @MainThread
    protected fun dismissDialog() {
        if (!mUiChangeLiveData.dismissLoadingDialogEvent.hasObservers()) {
            throw RuntimeException("Activity 或 Fragment 复写了 isNeedLoadingDialog() 方法并返回 false 时，无法使用 Dialog")
        }
        mUiChangeLiveData.dismissLoadingDialogEvent.call()
    }

    @MainThread
    protected fun showLoadSirSuccess() {
        mUiChangeLiveData.loadSirEvent.value = null
    }

    @MainThread
    protected fun showLoadSir(clz: Class<*>) {
        mUiChangeLiveData.loadSirEvent.value = clz
    }

    @MainThread
    protected fun finish() {
        if (!mUiChangeLiveData.finishEvent.hasObservers() || !GlobalConfig.isViewModelNeedStartAndFinish) {
            throw RuntimeException("GlobalConfig.isViewModelNeedStartAndFinish 设置为 false，或者 Activity/Fragment 复写了 isViewModelNeedStartAndFinish() 方法并返回 false 时，无法使用 finish() 方法")
        }
        mUiChangeLiveData.finishEvent.call()
    }

    @MainThread
    protected fun startActivity(clazz: Class<*>) {
        if (!mUiChangeLiveData.startActivityEvent.hasObservers() || !GlobalConfig.isViewModelNeedStartAndFinish) {
            throw RuntimeException("GlobalConfig.isViewModelNeedStartAndFinish 设置为 false，或者 Activity/Fragment 复写了 isViewModelNeedStartAndFinish() 方法并返回 false 时，无法使用 startActivity() 方法")
        }
        mUiChangeLiveData.startActivityEvent.value = clazz
    }

    @MainThread
    protected fun startActivity(clazz: Class<*>, bundle: Bundle?) {
        if (!mUiChangeLiveData.startActivityEventWithBundle.hasObservers() || !GlobalConfig.isViewModelNeedStartAndFinish) {
            throw RuntimeException("GlobalConfig.isViewModelNeedStartAndFinish 设置为 false，或者 Activity/Fragment 复写了 isViewModelNeedStartAndFinish() 方法并返回 false 时，无法使用 startActivity() 方法")
        }
        mUiChangeLiveData.startActivityEventWithBundle.value = Pair(clazz, bundle)
    }

    class UiChangeLiveData() {
        // 这里奇怪的是 lazy 中的泛型不能忽略

        val showLoadingDialogEvent: SingleLiveEvent<String?> by lazy {
            SingleLiveEvent<String?>()
        }
        val dismissLoadingDialogEvent: SingleLiveEvent<Any?> by lazy {
            SingleLiveEvent<Any?>()
        }

        val startActivityEvent: SingleLiveEvent<Class<*>> by lazy {
            SingleLiveEvent<Class<*>>()
        }
        val startActivityEventWithBundle: SingleLiveEvent<Pair<Class<*>, Bundle?>> by lazy {
            SingleLiveEvent<Pair<Class<*>, Bundle?>>()
        }
        val finishEvent: SingleLiveEvent<Any?> by lazy {
            SingleLiveEvent<Any?>()
        }

        val loadSirEvent: SingleLiveEvent<Class<*>?> by lazy {
            SingleLiveEvent<Class<*>?>()
        }
    }

    companion object {
        const val extraBundle = "extraBundle"
    }
}