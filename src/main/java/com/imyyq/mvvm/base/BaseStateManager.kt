package com.imyyq.mvvm.base

import androidx.core.util.Consumer
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

/**
 * 基础状态管理类，每个子类都应该维护一个状态，当有观察者注册监听时，应该把当前状态给观察者。
 *
 * 事件回调有优先级，默认是 0，在 [addListener] 时确定，越大优先级越高
 *
 * @author imyyq.star@gmail.com
 */
abstract class BaseStateManager<L> {
    private val LISTENERS = TreeMap<Int, CopyOnWriteArraySet<L>>()

    fun addListener(listener: L, priority: Int = 0, isCallOnAddListener: Boolean = true) {
        var set = LISTENERS[priority]
        if (set == null) {
            synchronized(this.javaClass) {
                if (set == null) {
                    set = CopyOnWriteArraySet()
                    LISTENERS[priority] = set!!
                }
            }
        }
        if (set!!.add(listener) && isCallOnAddListener) {
            onAddListener(listener)
        }
    }

    fun removeListener(listener: L) {
        for ((_, set) in LISTENERS) {
            for (l in set) {
                if (l === listener) {
                    set.remove(l)
                    break
                }
            }
        }
    }

    /**
     * 遍历监听者，按照优先级，优先级越高越先遍历。
     *
     * @param consumer 消费监听者
     */
    fun forEach(consumer: Consumer<L>) {
        if (LISTENERS.isEmpty()) return

        val list = ArrayList(LISTENERS.keys)
        for (i in list.indices.reversed()) {
            val set = LISTENERS[list[i]] ?: continue
            for (l in set) {
                try {
                    consumer.accept(l)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    protected abstract fun onAddListener(listener: L)

    companion object {
        private const val TAG = "BaseStateManager"
    }
}