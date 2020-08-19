package com.imyyq.mvvm.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.collection.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.imyyq.mvvm.bus.LiveDataBus
import com.imyyq.mvvm.utils.Utils
import com.imyyq.mvvm.widget.CustomLayoutDialog
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir

/**
 * 通过构造函数和泛型，完成 view 的初始化和 vm 的初始化，并且将它们绑定，
 */
abstract class ViewBindingBaseActivity<V : ViewBinding, VM : BaseViewModel<out BaseModel>> :
    ParallaxSwipeBackActivity(),
    IView<V, VM>, ILoadingDialog, ILoading, IActivityResult, IArgumentsFromIntent {

    protected lateinit var mBinding: V
    protected lateinit var mViewModel: VM

    private lateinit var mStartActivityForResult: ActivityResultLauncher<Intent>

    // 保证只有主线程访问这个变量，所以 lazy 不需要同步机制
    private val mLoadingDialog by lazy(mode = LazyThreadSafetyMode.NONE) { CustomLayoutDialog(this, loadingDialogLayout()) }

    private lateinit var mLoadService: LoadService<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = initBinding(layoutInflater, null)
        initViewAndViewModel()
        initParam()
        initUiChangeLiveData()
        initViewObservable()
        initLoadSir()
        initData()
    }

    abstract override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): V

    @CallSuper
    override fun initViewAndViewModel() {
        setContentView(mBinding.root)
        mViewModel = initViewModel(this)
        mViewModel.mIntent = getArgumentsIntent()
        // 让 vm 可以感知 v 的生命周期
        lifecycle.addObserver(mViewModel)
    }

    final override fun initUiChangeLiveData() {
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
                Observer {
                    setResult(it)
                    finish()
                },
                true
            )
            LiveDataBus.observe<Pair<Int?, Intent?>>(
                this,
                mViewModel.mUiChangeLiveData.setResultEvent!!,
                Observer { setResult(it) },
                true
            )
            // vm 可以启动界面
            LiveDataBus.observe<Class<out Activity>>(
                this,
                mViewModel.mUiChangeLiveData.startActivityEvent!!,
                Observer {
                    startActivity(it)
                },
                true
            )
            LiveDataBus.observe<Pair<Class<out Activity>, ArrayMap<String, *>>>(this,
                mViewModel.mUiChangeLiveData.startActivityWithMapEvent!!,
                Observer {
                    startActivity(it?.first, it?.second)
                },
                true
            )
            // vm 可以启动界面，并携带 Bundle，接收方可调用 getBundle 获取
            LiveDataBus.observe<Pair<Class<out Activity>, Bundle?>>(this,
                mViewModel.mUiChangeLiveData.startActivityEventWithBundle!!,
                Observer {
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
                Observer {
                    startActivityForResult(it)
                },
                true
            )
            // vm 可以启动界面，并携带 Bundle，接收方可调用 getBundle 获取
            LiveDataBus.observe<Pair<Class<out Activity>, Bundle?>>(
                this,
                mViewModel.mUiChangeLiveData.startActivityForResultEventWithBundle!!,
                Observer {
                    startActivityForResult(it?.first, bundle = it?.second)
                },
                true
            )
            LiveDataBus.observe<Pair<Class<out Activity>, ArrayMap<String, *>>>(
                this,
                mViewModel.mUiChangeLiveData.startActivityForResultEventWithMap!!,
                Observer {
                    startActivityForResult(it?.first, it?.second)
                },
                true
            )
        }

        if (isNeedLoadingDialog()) {
            mViewModel.mUiChangeLiveData.initLoadingDialogEvent()

            // 显示对话框
            mViewModel.mUiChangeLiveData.showLoadingDialogEvent?.observe(this, Observer {
                showLoadingDialog(mLoadingDialog, it)
            })
            // 隐藏对话框
            mViewModel.mUiChangeLiveData.dismissLoadingDialogEvent?.observe(this, Observer {
                dismissLoadingDialog(mLoadingDialog)
            })
        }
    }

    final override fun initLoadSir() {
        // 只有目标不为空的情况才有实例化的必要
        if (getLoadSirTarget() != null) {
            mLoadService = LoadSir.getDefault().register(
                getLoadSirTarget()
            ) { onLoadSirReload() }

            mViewModel.mUiChangeLiveData.initLoadSirEvent()
            mViewModel.mUiChangeLiveData.loadSirEvent?.observe(this, Observer {
                if (it == null) {
                    mLoadService.showSuccess()
                    onLoadSirSuccess()
                } else {
                    mLoadService.showCallback(it)
                    onLoadSirShowed(it)
                }
            })
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
        initStartActivityForResult()
        mStartActivityForResult.launch(Utils.getIntentByMapOrBundle(this, clz, map, bundle))
    }

    private fun initStartActivityForResult() {
        if (!this::mStartActivityForResult.isInitialized) {
            mStartActivityForResult =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    val data = it.data ?: Intent()
                    when (it.resultCode) {
                        Activity.RESULT_OK -> {
                            onActivityResultOk(data)
                            mViewModel.onActivityResultOk(data)
                        }
                        Activity.RESULT_CANCELED -> {
                            onActivityResultCanceled(data)
                            mViewModel.onActivityResultCanceled(data)
                        }
                        else -> {
                            onActivityResult(it.resultCode, data)
                            mViewModel.onActivityResult(it.resultCode, data)
                        }
                    }
                }
        }
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
        liveData.observe(this, Observer { onChanged(it) })

    /**
     * 如果加载中对话框可手动取消，并且开启了取消耗时任务的功能，则在取消对话框后调用取消耗时任务
     */
    @CallSuper
    override fun onCancelLoadingDialog() = mViewModel.cancelConsumingTask()

    override fun onDestroy() {
        super.onDestroy()

        // 界面销毁时移除 vm 的生命周期感知
        lifecycle.removeObserver(mViewModel)
        removeLiveDataBus(this)
    }
}