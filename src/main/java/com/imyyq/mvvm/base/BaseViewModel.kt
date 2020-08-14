package com.imyyq.mvvm.base

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import com.imyyq.mvvm.R
import com.imyyq.mvvm.app.CheckUtil
import com.imyyq.mvvm.app.RepositoryManager
import com.imyyq.mvvm.bus.LiveDataBus
import com.imyyq.mvvm.http.HttpHandler
import com.imyyq.mvvm.utils.SingleLiveEvent
import com.kingja.loadsir.callback.Callback
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

open class BaseViewModel<M : BaseModel>(app: Application) : AndroidViewModel(app), IViewModel,
    IActivityResult, IArgumentsFromBundle, IArgumentsFromIntent {

    constructor(app: Application, model: M) : this(app) {
        isAutoCreateRepo = false
        mModel = model
    }

    /**
     * 可能存在没有仓库的 vm，但我们这里也不要是可 null 的。
     * 如果 vm 没有提供仓库，说明此变量不可用，还去使用的话自然就报错。
     */
    protected lateinit var mModel: M

    private lateinit var mCompositeDisposable: Any
    private lateinit var mCallList: MutableList<Call<*>>
    private lateinit var mCoroutineScope: CoroutineScope

    val mUiChangeLiveData by lazy { UiChangeLiveData() }

    var mBundle: Bundle? = null
    var mIntent: Intent? = null

    /**
     * 是否自动创建仓库，默认是 true，
     */
    private var isAutoCreateRepo = true

    /**
     * 是否缓存自动创建的仓库，默认是 true
     */
    protected open fun isCacheRepo() = true

    /**
     * 所有网络请求都在 mCoroutineScope 域中启动协程，当页面销毁时会自动取消
     */
    fun <T> launch(
        block: suspend CoroutineScope.() -> IBaseResponse<T?>?,
        onSuccess: (() -> Unit)? = null,
        onResult: ((t: T) -> Unit),
        onFailed: ((code: Int, msg: String?) -> Unit)? = null,
        onComplete: (() -> Unit)? = null
    ) {
        initCoroutineScope()
        mCoroutineScope.launch {
            try {
                HttpHandler.handleResult(block(), onSuccess, onResult, onFailed)
            } catch (e: Exception) {
                onFailed?.let { HttpHandler.handleException(e, it) }
            } finally {
                onComplete?.invoke()
            }
        }
    }

    private fun initCoroutineScope() {
        if (!this::mCoroutineScope.isInitialized) {
            mCoroutineScope = CoroutineScope(Job() + Dispatchers.Main)
        }
    }

    /**
     * 发起协程，让协程和 UI 相关
     */
    fun launchUI(block: suspend CoroutineScope.() -> Unit) {
        initCoroutineScope()
        mCoroutineScope.launch { block() }
    }

    /**
     * 发起流
     */
    fun <T> launchFlow(block: suspend () -> T): Flow<T> {
        return flow {
            emit(block())
        }
    }

    @CallSuper
    override fun onCreate(owner: LifecycleOwner) {
        if (isAutoCreateRepo) {
            if (!this::mModel.isInitialized) {
                val modelClass: Class<M>?
                val type: Type? = javaClass.genericSuperclass
                modelClass = if (type is ParameterizedType) {
                    @Suppress("UNCHECKED_CAST")
                    type.actualTypeArguments[0] as? Class<M>
                } else null
                if (modelClass != null && modelClass != BaseModel::class.java) {
                    mModel = RepositoryManager.getRepo(modelClass, isCacheRepo())
                }
            }
        }
    }

    @CallSuper
    override fun onCleared() {
        // 可能 mModel 是未初始化的
        if (this::mModel.isInitialized) {
            mModel.onCleared()
        }

        LiveDataBus.removeObserve(this)
        LiveDataBus.removeStickyObserver(this)
        cancelConsumingTask()
    }

    /**
     * 取消耗时任务，比如在界面销毁时，或者在对话框消失时
     */
    fun cancelConsumingTask() {
        // ViewModel销毁时会执行，同时取消所有异步任务
        if (this::mCompositeDisposable.isInitialized) {
            (mCompositeDisposable as CompositeDisposable).clear()
        }
        if (this::mCallList.isInitialized) {
            mCallList.forEach { it.cancel() }
            mCallList.clear()
        }
        if (this::mCoroutineScope.isInitialized) {
            mCoroutineScope.cancel()
        }
    }

    /**
     * 给 Rx 使用的，如果项目中有使用到 Rx 异步相关的，在订阅时需要把订阅管理起来。
     * 通常异步操作都是在 vm 中进行的，管理起来的目的是让异步操作在界面销毁时也一起销毁，避免造成内存泄露
     */
    fun addSubscribe(disposable: Any) {
        if (!this::mCompositeDisposable.isInitialized) {
            mCompositeDisposable = CompositeDisposable()
        }
        (mCompositeDisposable as CompositeDisposable).add(disposable as Disposable)
    }

    /**
     * 不使用 Rx，使用 Retrofit 原生的请求方式
     */
    fun addCall(call: Any) {
        if (!this::mCallList.isInitialized) {
            mCallList = mutableListOf()
        }
        mCallList.add(call as Call<*>)
    }

    // 以下是加载中对话框相关的 =========================================================

    @MainThread
    protected fun showLoadingDialog() {
        showLoadingDialog(getApplication<Application>().getString(R.string.please_wait))
    }

    @MainThread
    protected fun showLoadingDialog(msg: String?) {
        CheckUtil.checkLoadingDialogEvent(mUiChangeLiveData.showLoadingDialogEvent)
        mUiChangeLiveData.showLoadingDialogEvent?.value = msg
    }

    @MainThread
    protected fun dismissLoadingDialog() {
        CheckUtil.checkLoadingDialogEvent(mUiChangeLiveData.dismissLoadingDialogEvent)
        mUiChangeLiveData.dismissLoadingDialogEvent?.call()
    }

    // 以下是内嵌加载中布局相关的 =========================================================

    @MainThread
    protected fun showLoadSirSuccess() {
        CheckUtil.checkLoadSirEvent(mUiChangeLiveData.loadSirEvent)
        mUiChangeLiveData.loadSirEvent?.value = null
    }

    @MainThread
    protected fun showLoadSir(clz: Class<out Callback>) {
        CheckUtil.checkLoadSirEvent(mUiChangeLiveData.loadSirEvent)
        mUiChangeLiveData.loadSirEvent?.value = clz
    }

    // 以下是界面开启和结束相关的 =========================================================

    @MainThread
    protected fun finish() {
        CheckUtil.checkStartAndFinishEvent(mUiChangeLiveData.finishEvent)
        LiveDataBus.send(mUiChangeLiveData.finishEvent!!, "")
    }

    @MainThread
    protected fun startActivity(clazz: Class<out Activity>) {
        CheckUtil.checkStartAndFinishEvent(mUiChangeLiveData.startActivityEvent)
        LiveDataBus.send(mUiChangeLiveData.startActivityEvent!!, clazz)
    }

    @MainThread
    protected fun startActivity(clazz: Class<out Activity>, map: Map<String, *>) {
        CheckUtil.checkStartAndFinishEvent(mUiChangeLiveData.startActivityWithMapEvent)
        LiveDataBus.send(mUiChangeLiveData.startActivityWithMapEvent!!, Pair(clazz, map))
    }

    @MainThread
    protected fun startActivity(clazz: Class<out Activity>, bundle: Bundle?) {
        CheckUtil.checkStartAndFinishEvent(mUiChangeLiveData.startActivityEventWithBundle)
        LiveDataBus.send(mUiChangeLiveData.startActivityEventWithBundle!!, Pair(clazz, bundle))
    }

    @MainThread
    protected fun startActivityForResult(clazz: Class<out Activity>) {
        CheckUtil.checkStartForResultEvent(mUiChangeLiveData.startActivityForResultEvent)
        LiveDataBus.send(mUiChangeLiveData.startActivityForResultEvent!!, clazz)
    }

    @MainThread
    protected fun startActivityForResult(clazz: Class<out Activity>, bundle: Bundle?) {
        CheckUtil.checkStartForResultEvent(mUiChangeLiveData.startActivityForResultEventWithBundle)
        LiveDataBus.send(mUiChangeLiveData.startActivityForResultEventWithBundle!!, Pair(clazz, bundle))
    }

    @MainThread
    protected fun startActivityForResult(clazz: Class<out Activity>, map: Map<String, *>) {
        CheckUtil.checkStartForResultEvent(mUiChangeLiveData.startActivityForResultEventWithMap)
        LiveDataBus.send(mUiChangeLiveData.startActivityForResultEventWithMap!!, Pair(clazz, map))
    }


    // ===================================================================================

    /**
     * 通用的 Ui 改变变量
     */
    class UiChangeLiveData {
        var showLoadingDialogEvent: SingleLiveEvent<String?>? = null
        var dismissLoadingDialogEvent: SingleLiveEvent<Any?>? = null

        var startActivityEvent: String? = null
        var startActivityWithMapEvent: String? = null
        var startActivityEventWithBundle: String? = null

        var startActivityForResultEvent: String? = null
        var startActivityForResultEventWithMap: String? = null
        var startActivityForResultEventWithBundle: String? = null

        var finishEvent: String? = null

        var loadSirEvent: SingleLiveEvent<Class<out Callback>?>? = null

        fun initLoadSirEvent() {
            loadSirEvent = SingleLiveEvent()
        }

        fun initLoadingDialogEvent() {
            showLoadingDialogEvent = SingleLiveEvent()
            dismissLoadingDialogEvent = SingleLiveEvent()
        }

        fun initStartActivityForResultEvent() {
            startActivityForResultEvent = UUID.randomUUID().toString()
            startActivityForResultEventWithMap = UUID.randomUUID().toString()
            startActivityForResultEventWithBundle = UUID.randomUUID().toString()
        }

        fun initStartAndFinishEvent() {
            startActivityEvent = UUID.randomUUID().toString()
            startActivityWithMapEvent = UUID.randomUUID().toString()
            startActivityEventWithBundle = UUID.randomUUID().toString()
            finishEvent = UUID.randomUUID().toString()
        }
    }

    override fun getBundle(): Bundle? = mBundle

    override fun getArgumentsIntent(): Intent? = mIntent
}