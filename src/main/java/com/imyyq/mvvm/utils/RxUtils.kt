package com.imyyq.mvvm.utils

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 有关 Rx 的工具类
 *
 * @author imyyq
 */
object RxUtils {
    /**
     * 线程调度器
     */
    @JvmStatic
    fun <T> schedulersTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream: Observable<T> ->
            upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }
}