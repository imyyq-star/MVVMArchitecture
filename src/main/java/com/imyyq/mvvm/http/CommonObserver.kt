package com.imyyq.mvvm.http

import android.util.Log
import androidx.annotation.CallSuper
import com.imyyq.mvvm.BuildConfig
import com.imyyq.mvvm.base.IBaseResponse
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

/**
 * 通用的 Rx 网络请求订阅接口
 */
abstract class CommonObserver<R> : Observer<IBaseResponse<R>> {
    lateinit var mBaseResult: IBaseResponse<R>

    /**
     * 请求完成，不管是成功还是失败
     */
    override fun onComplete() {
    }

    /**
     * 订阅开始
     */
    @CallSuper
    override fun onSubscribe(d: Disposable) {
        onStart()
    }

    /**
     * 请求有结果了
     */
    @CallSuper
    override fun onNext(entity: IBaseResponse<R>) {
        mBaseResult = entity
        val code = entity.code()
        val msg = entity.msg()
        // 防止状态码为 null
        if (code == null) {
            onFailed(entityCodeNullable, msgEntityCodeNullable)
            return
        }

        if (entity.isSuccess()) {
            onSuccess()
            entity.data()?.let { onResult(it) };
        } else {
            onFailed(code, msg)
        }
    }

    /**
     * 请求失败
     */
    @CallSuper
    override fun onError(e: Throwable) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
        return if (e is HttpException) {
            onFailed(e.code(), e.message())
        } else {
            val log = Log.getStackTraceString(e)
            onFailed(
                notHttpException,
                "$msgNotHttpException, 具体错误是\n${if (log.isEmpty()) e.message else log}"
            )
        }
    }

    /**
     * 发起网络请求，哪个线程开启的订阅，这个方法就在哪个线程响应，跟 subscribeOn 和 observeOn 无关
     */
    open fun onStart() {
    }

    /**
     * 请求成功，不管结果怎么样，服务器已经给出反应
     */
    open fun onSuccess() {
    }

    /**
     * 请求成功且有结果了，即状态码为成功的，且有数据了
     */
    abstract fun onResult(result: R)

    /**
     * 请求失败，可能是没网络等非服务器错误，或者是服务器返回失败
     */
    open fun onFailed(code: Int, msg: String?) {
    }
}