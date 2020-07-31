@file:Suppress("UNCHECKED_CAST")

package com.imyyq.mvvm.bus

import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import androidx.core.util.Pair
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

object LiveDataBus {
    /**
     * LiveData 支持生命周期感知，所以 key 是 LifecycleOwner
     * 每一个页面可以有多个监听，所以 value 也是个 map。
     * 而监听的区分者，可以是任意的，所以是 any，发送的数据也可以是任意的，所以 LiveData 也是 any 的
     */
    private val mLiveDataMap = ArrayMap<LifecycleOwner, ArrayMap<Any, MutableLiveData<Any>>>()

    /**
     * LiveData 还可以无生命周期感知，像 EventBus 一样。
     * 所以 key 是 Any 任意的。value 是这个 key 对应的所有监听。监听也可以有多个，所以是个 map。
     * 每一个 LiveData 都对应一个 Observer，用来解除监听的
     */
    private val mLiveDataForeverMap =
        ArrayMap<Any, ArrayMap<Any, Pair<MutableLiveData<Any>, Observer<Any>>>>()

    /**
     * 生命周期类型的监听
     */
    fun <R : Any> observe(
        owner: LifecycleOwner,
        tag: Any,
        observer: Observer<R>
    ) {
        val liveData = MutableLiveData<R>()

        var map = mLiveDataMap[owner]
        if (map == null) {
            map = arrayMapOf()
            mLiveDataMap[owner] = map
        }
        map[tag] = liveData as MutableLiveData<Any>

        liveData.observe(owner, observer)
    }

    /**
     * 解除监听，在 onDestroy 中调用
     */
    fun removeObserve(owner: LifecycleOwner) {
        mLiveDataMap.remove(owner)
    }

    /**
     * 无生命周期的监听，任意类都可以使用，比如 vm 中，不过需要注意的是，在不需要的时候需要手动移除监听，就像 EventBus 一样
     */
    fun <R : Any> observeForever(
        obj: Any,
        tag: Any,
        observer: Observer<R>
    ) {
        val liveData = MutableLiveData<R>()
        var objMap = mLiveDataForeverMap[obj]
        if (objMap == null) {
            objMap = arrayMapOf()
            mLiveDataForeverMap[obj] = objMap
        }
        objMap[tag] = Pair(liveData as MutableLiveData<Any>, observer as Observer<Any>)
        liveData.observeForever(observer)
    }

    /**
     * 移除 [obj] 对象下的所有监听
     */
    fun removeObserveForever(obj: Any) {
        mLiveDataForeverMap[obj]?.forEach {
            removeObserveForever(obj, it.key)
        }
        mLiveDataForeverMap.remove(obj)
    }

    /**
     * 移除 [obj] 对象下的 [tag] 监听
     */
    fun removeObserveForever(obj: Any, tag: Any) {
        val objMap = mLiveDataForeverMap[obj]
        val pair = objMap?.get(tag)
        pair?.let {
            it.first?.removeObserver(it.second!!)
            objMap.remove(pair)
        }
    }

    /**
     * 发送消息，只要是相同的 tag，就会触发对应的 LiveData，不管这个 tag 是在哪里注册的。
     */
    fun send(tag: Any, result: Any) {
        mLiveDataMap.forEach {
            it.value.forEach inside@{ entry ->
                if (entry.key == tag) {
                    entry.value.postValue(result)
                    return@inside
                }
            }
        }

        mLiveDataForeverMap.forEach {
            it.value.forEach inside@{ entry ->
                if (entry.key == tag) {
                    entry.value.first?.postValue(result)
                    return@inside
                }
            }
        }
    }
}