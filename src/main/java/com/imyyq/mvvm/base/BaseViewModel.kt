package com.imyyq.mvvm.base

import android.app.Application
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.imyyq.mvvm.utils.SingleLiveEvent

open class BaseViewModel<M : BaseModel>(app: Application) : AndroidViewModel(app), IViewModel {
    constructor(app: Application, model: M) : this(app) {
        mModel = model
    }

    protected var mModel: M? = null

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
        mModel?.onCleared()
    }

    @MainThread
    protected fun showDialog() {
        showDialog("请稍后...")
    }

    @MainThread
    protected fun showDialog(title: String?) {
        mUiChangeLiveData.showDialogEvent.value = title
    }

    @MainThread
    protected fun dismissDialog() {
        mUiChangeLiveData.dismissDialogEvent.call()
    }

    @MainThread
    protected fun finish() = mUiChangeLiveData.finishEvent.call()

    @MainThread
    protected fun startActivity(clazz: Class<*>) {
        mUiChangeLiveData.startActivityEvent.value = clazz
    }

    @MainThread
    protected fun startActivity(clazz: Class<*>, bundle: Bundle) {
        mUiChangeLiveData.startActivityEventWithBundle.value = Pair(clazz, bundle)
    }

    class UiChangeLiveData() {
        // 这里奇怪的是 lazy 中的泛型不能忽略

        val showDialogEvent: SingleLiveEvent<String?> by lazy {
            SingleLiveEvent<String?>()
        }
        val dismissDialogEvent: SingleLiveEvent<Any?> by lazy {
            SingleLiveEvent<Any?>()
        }

        val startActivityEvent: SingleLiveEvent<Class<*>> by lazy {
            SingleLiveEvent<Class<*>>()
        }
        val startActivityEventWithBundle: SingleLiveEvent<Pair<Class<*>, Bundle>> by lazy {
            SingleLiveEvent<Pair<Class<*>, Bundle>>()
        }
        val finishEvent: SingleLiveEvent<Any?> by lazy {
            SingleLiveEvent<Any?>()
        }
    }

    companion object {
        const val extraBundle = "extraBundle"
    }
}