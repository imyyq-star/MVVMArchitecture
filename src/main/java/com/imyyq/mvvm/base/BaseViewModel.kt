package com.imyyq.mvvm.base

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.collection.ArrayMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.imyyq.mvvm.R
import com.imyyq.mvvm.app.CheckUtil
import com.imyyq.mvvm.app.RepositoryManager
import com.imyyq.mvvm.bus.LiveDataBus
import com.imyyq.mvvm.http.HttpHandler
import com.imyyq.mvvm.utils.SingleLiveEvent
import com.imyyq.mvvm.utils.Utils
import com.imyyq.mvvm.utils.isInUIThread
import com.kingja.loadsir.callback.Callback
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * ViewModel 层的基类
 */
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
    lateinit var mModel: M

    private lateinit var mJobList: MutableList<Job>
    internal val mUiChangeLiveData by lazy { UiChangeLiveData() }

    internal var mBundle: Bundle? = null
    internal var mIntent: Intent? = null

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
        val job = GlobalScope.launch(Dispatchers.Main) {
            try {
                HttpHandler.handleResult(block(), onSuccess, onResult, onFailed)
            } catch (e: Exception) {
                e.printStackTrace()
                onFailed?.let { HttpHandler.handleException(e, it) }
            } finally {
                onComplete?.invoke()
            }
        }
        addJob(job)
    }

    /**
     * 发起协程，让协程和 UI 相关
     */
    fun launchUI(block: suspend CoroutineScope.() -> Unit) {
        val job = GlobalScope.launch(Dispatchers.Main) { block() }
        addJob(job)
    }

    /**
     * 发起协程，让协程在 IO 线程中执行
     */
    fun launchIO(block: suspend CoroutineScope.() -> Unit) {
        val job = GlobalScope.launch(Dispatchers.IO) { block() }
        addJob(job)
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
        if (this::mJobList.isInitialized) {
            mJobList.forEach { it.cancel() }
            mJobList.clear()
        }
    }

    /**
     * [ViewModel.viewModelScope] 是生命周期安全的，无需手动管理。
     * 此方法用于存储多个 Job 实例，以便可以取消它们。
     */
    fun addJob(job: Job) {
        if (!this::mJobList.isInitialized) {
            mJobList = mutableListOf()
        }

        mJobList.add(job)
    }

    // 以下是加载中对话框相关的 =========================================================

    fun showLoadingDialog() {
        showLoadingDialog(getApplication<Application>().getString(R.string.please_wait))
    }

    fun showLoadingDialog(msg: String?) {
        CheckUtil.checkLoadingDialogEvent(mUiChangeLiveData.showLoadingDialogEvent)
        if (isInUIThread()) {
            mUiChangeLiveData.showLoadingDialogEvent?.value = msg
        } else {
            mUiChangeLiveData.showLoadingDialogEvent?.postValue(msg)
        }
    }

    fun dismissLoadingDialog() {
        CheckUtil.checkLoadingDialogEvent(mUiChangeLiveData.dismissLoadingDialogEvent)
        mUiChangeLiveData.dismissLoadingDialogEvent?.call()
    }

    // 以下是内嵌加载中布局相关的 =========================================================

    fun showLoadSirSuccess() {
        CheckUtil.checkLoadSirEvent(mUiChangeLiveData.loadSirEvent)
        if (isInUIThread()) {
            mUiChangeLiveData.loadSirEvent?.value = null
        } else {
            mUiChangeLiveData.loadSirEvent?.postValue(null)
        }
    }

    fun showLoadSir(clz: Class<out Callback>) {
        CheckUtil.checkLoadSirEvent(mUiChangeLiveData.loadSirEvent)
        if (isInUIThread()) {
            mUiChangeLiveData.loadSirEvent?.value = clz
        } else {
            mUiChangeLiveData.loadSirEvent?.postValue(clz)
        }
    }

    // 以下是界面开启和结束相关的 =========================================================

    @MainThread
    fun setResult(
        resultCode: Int,
        map: ArrayMap<String, *>? = null,
        bundle: Bundle? = null
    ) {
        setResult(resultCode, Utils.getIntentByMapOrBundle(map = map, bundle = bundle))
    }

    @MainThread
    fun setResult(resultCode: Int, data: Intent? = null) {
        CheckUtil.checkStartAndFinishEvent(mUiChangeLiveData.setResultEvent)
        LiveDataBus.send(mUiChangeLiveData.setResultEvent!!, Pair(resultCode, data))
    }

    @MainThread
    fun finish(
        resultCode: Int? = null,
        map: ArrayMap<String, *>? = null,
        bundle: Bundle? = null
    ) {
        finish(resultCode, Utils.getIntentByMapOrBundle(map = map, bundle = bundle))
    }

    @MainThread
    fun finish(resultCode: Int? = null, data: Intent? = null) {
        CheckUtil.checkStartAndFinishEvent(mUiChangeLiveData.finishEvent)
        LiveDataBus.send(mUiChangeLiveData.finishEvent!!, Pair(resultCode, data))
    }

    fun startActivity(clazz: Class<out Activity>) {
        CheckUtil.checkStartAndFinishEvent(mUiChangeLiveData.startActivityEvent)
        LiveDataBus.send(mUiChangeLiveData.startActivityEvent!!, clazz)
    }

    fun startActivity(clazz: Class<out Activity>, map: ArrayMap<String, *>) {
        CheckUtil.checkStartAndFinishEvent(mUiChangeLiveData.startActivityWithMapEvent)
        LiveDataBus.send(mUiChangeLiveData.startActivityWithMapEvent!!, Pair(clazz, map))
    }

    fun startActivity(clazz: Class<out Activity>, bundle: Bundle?) {
        CheckUtil.checkStartAndFinishEvent(mUiChangeLiveData.startActivityEventWithBundle)
        LiveDataBus.send(mUiChangeLiveData.startActivityEventWithBundle!!, Pair(clazz, bundle))
    }

    fun startActivityForResult(clazz: Class<out Activity>) {
        CheckUtil.checkStartForResultEvent(mUiChangeLiveData.startActivityForResultEvent)
        LiveDataBus.send(mUiChangeLiveData.startActivityForResultEvent!!, clazz)
    }

    fun startActivityForResult(clazz: Class<out Activity>, bundle: Bundle?) {
        CheckUtil.checkStartForResultEvent(mUiChangeLiveData.startActivityForResultEventWithBundle)
        LiveDataBus.send(mUiChangeLiveData.startActivityForResultEventWithBundle!!, Pair(clazz, bundle))
    }

    fun startActivityForResult(clazz: Class<out Activity>, map: ArrayMap<String, *>) {
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
        var setResultEvent: String? = null

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
            setResultEvent = UUID.randomUUID().toString()
        }
    }

    override fun getBundle(): Bundle? = mBundle

    override fun getArgumentsIntent(): Intent? = mIntent
}