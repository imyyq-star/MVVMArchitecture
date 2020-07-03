package com.imyyq.mvvm.base

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imyyq.mvvm.BuildConfig
import com.imyyq.mvvm.R
import com.imyyq.mvvm.http.*
import com.imyyq.mvvm.utils.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

open class BaseViewModel<M : BaseModel>(app: Application) : AndroidViewModel(app), IViewModel, IActivityResult {
    constructor(app: Application, model: M) : this(app) {
        mModel = model
    }

    /**
     * 可能存在没有仓库的 vm，但我们这里也不要是可 null 的。
     * 如果 vm 没有提供仓库，说明此变量不可用，还去使用的话自然就报错。
     */
    protected lateinit var mModel: M

    private lateinit var mCompositeDisposable: Any

    val mUiChangeLiveData by lazy { UiChangeLiveData() }

    /**
     * 所有网络请求都在 viewModelScope 域中启动协程，当页面销毁时会自动取消
     */
    fun <T> launch(
        block: suspend CoroutineScope.() -> IBaseResponse<T?>?,
        onSuccess: (() -> Unit)? = null,
        onResult: ((t: T) -> Unit),
        onFailed: ((code: Int, msg: String?) -> Unit)? = null,
        onComplete: (() -> Unit)? = null
    ) = viewModelScope.launch {
        try {
            handleResult(withContext(Dispatchers.IO) { block() }, onSuccess, onResult, onFailed)
        } catch (e: Exception) {
            onFailed?.let { handleException(e, it) }
        } finally {
            onComplete?.invoke()
        }
    }

    /**
     * 发起协程，让协程和 UI 相关
     */
    fun launchUI(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch { block() }

    /**
     * 发起流
     */
    fun <T> launchFlow(block: suspend () -> T): Flow<T> {
        return flow {
            emit(block())
        }
    }

    /**
     * 处理请求结果
     *
     * [entity] 实体
     * [onSuccess] 状态码对了就回调
     * [onResult] 状态码对了，且实体不是 null 才回调
     * [onFailed] 有错误发生，可能是服务端错误，可能是数据错误，详见 code 错误码和 msg 错误信息
     */
    private fun <T> handleResult(
        entity: IBaseResponse<T?>?,
        onSuccess: (() -> Unit)? = null,
        onResult: ((t: T) -> Unit),
        onFailed: ((code: Int, msg: String?) -> Unit)? = null
    ) {
        // 防止实体为 null
        if (entity == null) {
            onFailed?.invoke(entityNullable, msgEntityNullable)
            return
        }
        val code = entity.code()
        val msg = entity.msg()
        // 防止状态码为 null
        if (code == null) {
            onFailed?.invoke(entityCodeNullable, msgEntityCodeNullable)
            return
        }
        // 请求成功
        if (entity.isSuccess()) {
            // 回调成功
            onSuccess?.invoke()
            // 实体不为 null 才有价值
            entity.data()?.let { onResult.invoke(it) }
        } else {
            // 失败了
            onFailed?.invoke(code, msg)
        }
    }

    /**
     * 处理异常
     */
    private fun handleException(
        e: Exception,
        onFailed: (code: Int, msg: String?) -> Unit
    ) {
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

    @CallSuper
    override fun onCleared() {
        // 可能 mModel 是未初始化的
        if (this::mModel.isInitialized) {
            mModel.onCleared()
        }

        // ViewModel销毁时会执行，同时取消所有异步任务
        if (this::mCompositeDisposable.isInitialized) {
            (mCompositeDisposable as CompositeDisposable).clear()
        }
    }

    /**
     * 给 Rx 使用的，如果项目中有使用到 Rx 异步相关的，在订阅时需要把订阅管理起来。
     * 通常异步操作都是在 vm 中进行的，管理起来的目的是让异步操作在界面销毁时也一起销毁，避免造成内存泄露
     */
    protected fun addSubscribe(disposable: Any) {
        if (!this::mCompositeDisposable.isInitialized) {
            mCompositeDisposable = CompositeDisposable()
        }
        (mCompositeDisposable as CompositeDisposable).add(disposable as Disposable)
    }

    // 以下是加载中对话框相关的 =========================================================

    @MainThread
    protected fun showLoadingDialog() {
        showLoadingDialog(getApplication<Application>().getString(R.string.please_wait))
    }

    @MainThread
    protected fun showLoadingDialog(msg: String?) {
        mUiChangeLiveData.showLoadingDialogEvent.value = msg
    }

    @MainThread
    protected fun dismissLoadingDialog() {
        mUiChangeLiveData.dismissLoadingDialogEvent.call()
    }

    // 以下是内嵌加载中布局相关的 =========================================================

    @MainThread
    protected fun showLoadSirSuccess() {
        mUiChangeLiveData.loadSirEvent.value = null
    }

    @MainThread
    protected fun showLoadSir(clz: Class<*>) {
        mUiChangeLiveData.loadSirEvent.value = clz
    }

    // 以下是界面开启和结束相关的 =========================================================

    @MainThread
    protected fun finish() {
        mUiChangeLiveData.finishEvent.call()
    }

    @MainThread
    protected fun startActivity(clazz: Class<*>) {
        mUiChangeLiveData.startActivityEvent.value = clazz
    }

    @MainThread
    protected fun startActivity(clazz: Class<*>, bundle: Bundle?) {
        mUiChangeLiveData.startActivityEventWithBundle.value = Pair(clazz, bundle)
    }

    @MainThread
    protected fun startActivityForResult(clazz: Class<*>) {
        mUiChangeLiveData.startActivityForResultEvent.value = clazz
    }

    @MainThread
    protected fun startActivityForResult(clazz: Class<*>, bundle: Bundle?) {
        mUiChangeLiveData.startActivityForResultEventWithBundle.value = Pair(clazz, bundle)
    }

    /**
     * 通用的 Ui 改变变量
     */
    class UiChangeLiveData {
        val showLoadingDialogEvent by lazy { SingleLiveEvent<String?>() }
        val dismissLoadingDialogEvent by lazy { SingleLiveEvent<Any?>() }

        val startActivityEvent by lazy { SingleLiveEvent<Class<*>>() }
        val startActivityEventWithBundle by lazy { SingleLiveEvent<Pair<Class<*>, Bundle?>>() }

        val startActivityForResultEvent by lazy { SingleLiveEvent<Class<*>>() }
        val startActivityForResultEventWithBundle by lazy { SingleLiveEvent<Pair<Class<*>, Bundle?>>() }

        val finishEvent by lazy { SingleLiveEvent<Any?>() }

        val loadSirEvent by lazy { SingleLiveEvent<Class<*>?>() }
    }

    companion object {
        const val extraBundle = "extraBundle"
    }
}