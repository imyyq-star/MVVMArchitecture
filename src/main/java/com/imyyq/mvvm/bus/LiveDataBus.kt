@file:Suppress("UNCHECKED_CAST")

package com.imyyq.mvvm.bus

import android.os.Looper
import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import androidx.core.util.Pair
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * 事件总线类，支持生命周期相关和无关的事件，支持粘性事件。
 * 生命周期无关的事件和粘性事件，需要手动移除监听者，框架的 Activity/Fragment 和
 * ViewModel 已经实现了自动移除。所以在这两者中使用无需手动移除。在其他对象中使用就需要在不需要监听时手动移除。
 *
 * @author imyyq.star@gmail.com
 */
object LiveDataBus {
    /**
     * registrants 是注册者
     * tag 是区分事件的标记。
     *
     * 下同。
     */
    private val mLiveDataMap =
        ArrayMap<Any /* registrants */, ArrayMap<Any /* tag */, Pair<MutableLiveData<Any>, Observer<Any>>>>()

    /**
     * 粘性事件，一个 tag 对应一个列表，每个元素表示一个注册者，包含的数据在 [StickyData]
     */
    private val mStickyLiveDataMap =
        ArrayMap<Any /* tag */, MutableList<StickyData>>()

    /**
     * 监听事件，[registrants] 如果是 V 和 VM，那么无需手动移除监听，框架已经实现了自动移除。
     * 否则需要在不需要监听的时候手动移除，就像 EventBus 一样
     *
     * [isForever] 默认值取决于 [registrants] 的类型，如果是 LifecycleOwner，即有生命周期的，则默认是 false，
     * 如果是 true 的话，那么事件将和生命周期无关，即在 A 页面监听，打开 B 页面发送事件，A 也可以收到。
     */
    fun <R : Any> observe(
        registrants: Any,
        tag: Any,
        observer: Observer<R>,
        isForever: Boolean = registrants !is LifecycleOwner
    ) {
        val liveData = MutableLiveData<R>()
        var objMap = mLiveDataMap[registrants]
        if (objMap == null) {
            objMap = arrayMapOf()
            mLiveDataMap[registrants] = objMap
        }

        if (objMap[tag] != null) {
            return
        }

        objMap[tag] = Pair(liveData as MutableLiveData<Any>, observer as Observer<Any>)
        if (registrants is LifecycleOwner && !isForever) {
            liveData.observe(registrants, observer)
        } else {
            liveData.observeForever(observer)
        }
    }

    /**
     * 移除 [registrants] 对象下的所有监听
     */
    fun removeObserve(registrants: Any) {
        mLiveDataMap[registrants]?.forEach {
            it.value?.first?.removeObserver(it.value?.second!!)
        }
        mLiveDataMap.remove(registrants)
    }

    /**
     * 移除 [registrants] 对象下的 [tag] 监听
     */
    fun removeObserve(registrants: Any, tag: Any) {
        val objMap = mLiveDataMap[registrants]
        objMap?.get(tag)?.let {
            it.first?.removeObserver(it.second!!)
            objMap.remove(tag)
        }
    }

    /**
     * 注册粘性事件，如果 [registrants] 是 LifecycleOwner，则生命周期可感知，否则是无感知的。
     * 都需要手动调用 [removeStickyObserver]，框架中 V 和 VM 中已实现移除，故在 V、VM 使用使用，无需手动移除
     */
    fun <R : Any> observeSticky(
        registrants: Any,
        tag: Any,
        observer: Observer<R>
    ) {
        // 一个 tag 对应多个监听者
        var list = mStickyLiveDataMap[tag]
        if (list == null) {
            list = mutableListOf()
            mStickyLiveDataMap[tag] = list
        }

        // 找到没有被人注册的 liveData
        var stickyData: StickyData? = null
        list.forEach {
            if (it.registrants == null) {
                stickyData = it
                return@forEach
            }
        }

        val obs = observer as Observer<Any>
        // 没有 liveData，则创建
        if (stickyData == null) {
            val liveData = MutableLiveData<Any>()
            // 新创建的 liveData，需要有效数据
            if (list.isNotEmpty()) {
                val data = list[0]
                if (data.isValueValid) {
                    liveData.postValue(data.liveData.value)
                }
            }
            stickyData = StickyData(liveData, registrants, obs, false)
            list.add(stickyData!!)
        } else {
            stickyData!!.registrants = registrants
            stickyData!!.observer = obs
        }
        if (registrants is LifecycleOwner) {
            stickyData!!.liveData.observe(registrants, obs)
        } else {
            stickyData!!.liveData.observeForever(obs)
        }
    }

    /**
     * 发送粘性事件
     */
    fun sendSticky(tag: Any, result: Any, inUiThread: Boolean = Looper.getMainLooper().thread == Thread.currentThread()) {
        var list = mStickyLiveDataMap[tag]
        if (list == null) {
            list = mutableListOf()
            mStickyLiveDataMap[tag] = list
        }
        if (list.isEmpty()) {
            val liveData: MutableLiveData<Any> = MutableLiveData()
            val data = StickyData(liveData, null, null, true)
            list.add(data)
            if (inUiThread) {
                liveData.value = result
            } else {
                liveData.postValue(result)
            }
        } else {
            list.forEach {
                it.isValueValid = true
                if (inUiThread) {
                    it.liveData.value = result
                } else {
                    it.liveData.postValue(result)
                }
            }
        }
    }

    /**
     * 移除粘性事件监听者
     */
    fun removeStickyObserver(registrants: Any) {
        mStickyLiveDataMap.forEach { entry ->
            entry.value?.forEach {
                val isRemove = registrants == it.registrants
                if (isRemove) {
                    it.observer?.let { observer -> it.liveData.removeObserver(observer) }
                    it.observer = null
                    it.registrants = null
                }
            }
        }
    }

    /**
     * 根据 [tag] 移除一类粘性事件，如果确定粘性事件不需要了，需要手动移除。
     */
    fun removeSticky(tag: Any) {
        val list = mStickyLiveDataMap[tag]
        list?.forEach {
            it.observer?.let { observer -> it.liveData.removeObserver(observer) }
            it.observer = null
            it.registrants = null
        }
        mStickyLiveDataMap.remove(tag)
    }

    /**
     * 发送消息，只要是相同的 tag，就会触发对应的 LiveData，不管这个 tag 是在哪里注册的。包括粘性事件，也可以接收普通的事件。
     */
    fun send(tag: Any, result: Any, inUiThread: Boolean = Looper.getMainLooper().thread == Thread.currentThread()) {
        mLiveDataMap.forEach {
            it.value.forEach inside@{ entry ->
                if (entry.key == tag) {
                    if (inUiThread) {
                        entry.value.first?.value = result
                    } else {
                        entry.value.first?.postValue(result)
                    }
                    return@inside
                }
            }
        }

        mStickyLiveDataMap[tag]?.forEach {
            if (inUiThread) {
                it.liveData.value = result
            } else {
                it.liveData.postValue(result)
            }
        }
    }
}