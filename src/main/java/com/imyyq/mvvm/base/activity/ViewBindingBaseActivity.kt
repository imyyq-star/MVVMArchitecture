package com.imyyq.mvvm.base.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.collection.ArrayMap
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding
import com.imyyq.mvvm.base.IActivityResult
import com.imyyq.mvvm.base.IArgumentsFromIntent
import com.imyyq.mvvm.base.model.BaseModel
import com.imyyq.mvvm.base.view.ILoading
import com.imyyq.mvvm.base.view.ILoadingDialog
import com.imyyq.mvvm.base.view.IView
import com.imyyq.mvvm.base.viewmodel.BaseViewModel
import com.imyyq.mvvm.bus.LiveDataBus
import com.imyyq.mvvm.utils.Utils
import com.imyyq.mvvm.utils.getViewBinding
import com.imyyq.mvvm.widget.CustomLayoutDialog
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir

/**
 * ViewBindingActivity 基类
 *
 * 通过构造函数和泛型，完成 view 的初始化和 vm 的初始化，并且将它们绑定，
 *
 * @author imyyq.star@gmail.com
 */
abstract class ViewBindingBaseActivity<V : ViewBinding, VM : BaseViewModel<out BaseModel>> :
    ParallaxSwipeBackActivity(),
    IView<V, VM>, ILoadingDialog, ILoading, IActivityResult, IArgumentsFromIntent {

    protected lateinit var mBinding: V
    protected lateinit var mViewModel: VM

    // 保证只有主线程访问这个变量，所以 lazy 不需要同步机制
    private val mLoadingDialog by lazy(mode = LazyThreadSafetyMode.NONE) { CustomLayoutDialog(this, loadingDialogLayout()) }

    private lateinit var mLoadService: LoadService<*>

    private val mStartActivityForResult: ActivityResultLauncher<Intent>?

    init {
        @Suppress("LeakingThis")
        val ok = onActivityResultOk()
        @Suppress("LeakingThis")
        val cancel = onActivityResultCanceled()

        mStartActivityForResult = if (ok != null || cancel != null) {
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    ok?.invoke(it.data)
                } else {
                    cancel?.invoke(it.data)
                }
            }
        } else {
            null
        }
    }

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = initBinding(layoutInflater, null)
        initContentView(mBinding.root)
        initViewModel()
        initParam()
        initUiChangeLiveData()
        initViewObservable()
        initLoadSir()
        initData()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): V = getViewBinding(inflater)

    open fun initContentView(contentView: View) {
        setContentView(contentView)
    }

    @CallSuper
    override fun initViewModel() {
        mViewModel = initViewModel(this)
        mViewModel.mIntent = getArgumentsIntent()
        // 让 vm 可以感知 v 的生命周期
        lifecycle.addObserver(mViewModel)
    }

    @CallSuper
    override fun initUiChangeLiveData() {
        if (isViewModelNeedStartAndFinish()) {
            mViewModel.mUiChangeLiveData.initStartAndFinishEvent()

            fun setResult(pair: Pair<Int?, Intent?>) {
                pair.first?.let { resultCode ->
                    val intent = pair.second
                    if (intent == null) {
                        setResult(resultCode)
                    } else {
                        setResult(resultCode, intent)
                    }
                }
            }

            // vm 可以结束界面
            LiveDataBus.observe<Pair<Int?, Intent?>>(
                this,
                mViewModel.mUiChangeLiveData.finishEvent!!,
                {
                    setResult(it)
                    finish()
                },
                true
            )
            LiveDataBus.observe<Pair<Int?, Intent?>>(
                this,
                mViewModel.mUiChangeLiveData.setResultEvent!!,
                { setResult(it) },
                true
            )
            // vm 可以启动界面
            LiveDataBus.observe<Class<out Activity>>(
                this,
                mViewModel.mUiChangeLiveData.startActivityEvent!!,
                {
                    startActivity(it)
                },
                true
            )
            LiveDataBus.observe<Pair<Class<out Activity>, ArrayMap<String, *>>>(this,
                mViewModel.mUiChangeLiveData.startActivityWithMapEvent!!,
                {
                    startActivity(it?.first, it?.second)
                },
                true
            )
            // vm 可以启动界面，并携带 Bundle，接收方可调用 getBundle 获取
            LiveDataBus.observe<Pair<Class<out Activity>, Bundle?>>(this,
                mViewModel.mUiChangeLiveData.startActivityEventWithBundle!!,
                {
                    startActivity(it?.first, bundle = it?.second)
                },
                true
            )
        }
        if (isViewModelNeedStartForResult()) {
            mViewModel.mUiChangeLiveData.initStartActivityForResultEvent()

            // vm 可以启动界面
            LiveDataBus.observe<Class<out Activity>>(
                this,
                mViewModel.mUiChangeLiveData.startActivityForResultEvent!!,
                { startActivityForResult(it) },
                true
            )
            // vm 可以启动界面，并携带 Bundle，接收方可调用 getBundle 获取
            LiveDataBus.observe<Pair<Class<out Activity>, Bundle?>>(
                this,
                mViewModel.mUiChangeLiveData.startActivityForResultEventWithBundle!!,
                {
                    startActivityForResult(it?.first, bundle = it?.second)
                },
                true
            )
            LiveDataBus.observe<Pair<Class<out Activity>, ArrayMap<String, *>>>(
                this,
                mViewModel.mUiChangeLiveData.startActivityForResultEventWithMap!!,
                {
                    startActivityForResult(it?.first, it?.second)
                },
                true
            )
        }

        if (isNeedLoadingDialog()) {
            mViewModel.mUiChangeLiveData.initLoadingDialogEvent()

            // 显示对话框
            mViewModel.mUiChangeLiveData.showLoadingDialogEvent?.observe(this) {
                showLoadingDialog(mLoadingDialog, it)
            }
            // 隐藏对话框
            mViewModel.mUiChangeLiveData.dismissLoadingDialogEvent?.observe(this) {
                dismissLoadingDialog(mLoadingDialog)
            }
        }
    }

    @CallSuper
    override fun initLoadSir() {
        // 只有目标不为空的情况才有实例化的必要
        if (getLoadSirTarget() != null) {
            mLoadService = LoadSir.getDefault().register(
                getLoadSirTarget()
            ) { onLoadSirReload() }

            mViewModel.mUiChangeLiveData.initLoadSirEvent()
            mViewModel.mUiChangeLiveData.loadSirEvent?.observe(this) {
                if (it == null) {
                    mLoadService.showSuccess()
                    onLoadSirSuccess()
                } else {
                    mLoadService.showCallback(it)
                    onLoadSirShowed(it)
                }
            }
        }
    }

    fun startActivity(
        clz: Class<out Activity>?,
        map: ArrayMap<String, *>? = null,
        bundle: Bundle? = null
    ) {
        startActivity(Utils.getIntentByMapOrBundle(this, clz, map, bundle))
    }

    fun startActivityForResult(
        clz: Class<out Activity>?,
        map: ArrayMap<String, *>? = null,
        bundle: Bundle? = null
    ) {
        if (mStartActivityForResult == null) {
            throw RuntimeException("请实现 onActivityResultCanceled 或 onActivityResultOk 方法")
        }
        mStartActivityForResult.launch(Utils.getIntentByMapOrBundle(this, clz, map, bundle))
    }

    /**
     * 通过 [BaseViewModel.startActivity] 传递 bundle，在这里可以获取
     */
    final override fun getBundle(): Bundle? {
        return intent.extras
    }

    final override fun getArgumentsIntent(): Intent? {
        return intent
    }

    /**
     * <pre>
     *     // 一开始我们这么写
     *     mViewModel.liveData.observe(this, Observer { })
     *
     *     // 用这个方法可以这么写
     *     observe(mViewModel.liveData) { }
     *
     *     // 或者这么写
     *     observe(mViewModel.liveData, this::onChanged)
     *     private fun onChanged(s: String) { }
     * </pre>
     */
    fun <T> observe(liveData: LiveData<T>, onChanged: ((t: T) -> Unit)) =
        liveData.observe(this) { onChanged(it) }

    /**
     * 如果加载中对话框可手动取消，并且开启了取消耗时任务的功能，则在取消对话框后调用取消耗时任务
     */
    @CallSuper
    override fun onCancelLoadingDialog() = mViewModel.cancelConsumingTask()

    /**
     * 不建议使用。
     * 请查看 [IActivityResult] 接口的描述
     */
    final override fun onActivityResult(requestCode: Int, resultCode: Int, dataIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, dataIntent)
    }

    override fun onDestroy() {
        super.onDestroy()

        // 界面销毁时移除 vm 的生命周期感知
        if (this::mViewModel.isInitialized) {
            lifecycle.removeObserver(mViewModel)
        }
        removeLiveDataBus(this)
    }
}