package com.imyyq.mvvm.utils

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 只有一个观察者能收到通知，并且只有明确调用了 setValue 的时候才会发出通知。
 * 反复注册观察并不会触发重新通知。
 *
 * @author imyyq.star@gmail.com
 */
class SingleLiveEvent<T> : MutableLiveData<T?>() {
    private val mPending =
        AtomicBoolean(false)

    @MainThread
    override fun observe(
        owner: LifecycleOwner,
        observer: Observer<in T?>
    ) {
        if (hasActiveObservers()) {
            LogUtil.w(
                TAG,
                "Multiple observers registered but only one will be notified of changes."
            )
        }

        // Observe the internal MutableLiveData
        super.observe(owner) { t: T? ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    override fun postValue(value: T?) {
        mPending.set(true)
        super.postValue(value)
    }

    fun call() = postValue(null)

    companion object {
        private const val TAG = "SingleLiveEvent"
    }
}