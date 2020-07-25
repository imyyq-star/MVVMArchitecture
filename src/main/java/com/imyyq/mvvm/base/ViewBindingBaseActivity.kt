package com.imyyq.mvvm.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.imyyq.mvvm.widget.CustomLayoutDialog
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import java.io.Serializable

/**
 * 通过构造函数和泛型，完成 view 的初始化和 vm 的初始化，并且将它们绑定，
 */
abstract class ViewBindingBaseActivity<V : ViewBinding, VM : BaseViewModel<out BaseModel>> :
    ParallaxSwipeBackActivity(),
    IView<V, VM>, ILoadingDialog, ILoading, IActivityResult, IArgumentsFromIntent {

    protected lateinit var mBinding: V
    protected lateinit var mViewModel: VM

    private lateinit var mStartActivityForResult: ActivityResultLauncher<Intent>

    private val mLoadingDialog by lazy { CustomLayoutDialog(this, loadingDialogLayout()) }

    private lateinit var mLoadService: LoadService<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isKeepScreenOn()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        mBinding = initBinding(layoutInflater, null)
        initViewAndViewModel()
        initParam()
        initUiChangeLiveData()
        initViewObservable()
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

    override fun onDestroy() {
        super.onDestroy()

        // 界面销毁时移除 vm 的生命周期感知
        lifecycle.removeObserver(mViewModel)
    }

    /**
     * 通过 [BaseViewModel.startActivity] 传递 bundle，在这里可以获取
     */
    override fun getBundle(): Bundle? {
        return intent.extras
    }

    override fun getArgumentsIntent(): Intent? {
        return intent
    }

    final override fun initUiChangeLiveData() {
        if (isViewModelNeedStartAndFinish()) {
            mViewModel.mUiChangeLiveData.initStartAndFinishEvent()

            // vm 可以结束界面
            mViewModel.mUiChangeLiveData.finishEvent?.observe(this, Observer { finish() })
            // vm 可以启动界面
            mViewModel.mUiChangeLiveData.startActivityEvent?.observe(this, Observer {
                val intent = Intent(this, it)
                startActivity(intent)
            })
            mViewModel.mUiChangeLiveData.startActivityWithMapEvent?.observe(this, Observer {
                val intent = Intent(this, it?.first)
                it?.second?.forEach { entry ->
                    @Suppress("UNCHECKED_CAST")
                    when (val value = entry.value) {
                        is Boolean -> {
                            intent.putExtra(entry.key, value)
                        }
                        is BooleanArray -> {
                            intent.putExtra(entry.key, value)
                        }
                        is Byte -> {
                            intent.putExtra(entry.key, value)
                        }
                        is ByteArray -> {
                            intent.putExtra(entry.key, value)
                        }
                        is Char -> {
                            intent.putExtra(entry.key, value)
                        }
                        is CharArray -> {
                            intent.putExtra(entry.key, value)
                        }
                        is Short -> {
                            intent.putExtra(entry.key, value)
                        }
                        is ShortArray -> {
                            intent.putExtra(entry.key, value)
                        }
                        is Int -> {
                            intent.putExtra(entry.key, value)
                        }
                        is IntArray -> {
                            intent.putExtra(entry.key, value)
                        }
                        is Long -> {
                            intent.putExtra(entry.key, value)
                        }
                        is LongArray -> {
                            intent.putExtra(entry.key, value)
                        }
                        is Float -> {
                            intent.putExtra(entry.key, value)
                        }
                        is FloatArray -> {
                            intent.putExtra(entry.key, value)
                        }
                        is Double -> {
                            intent.putExtra(entry.key, value)
                        }
                        is DoubleArray -> {
                            intent.putExtra(entry.key, value)
                        }
                        is String -> {
                            intent.putExtra(entry.key, value)
                        }
                        is CharSequence -> {
                            intent.putExtra(entry.key, value)
                        }
                        is Parcelable -> {
                            intent.putExtra(entry.key, value)
                        }
                        is Serializable -> {
                            intent.putExtra(entry.key, value)
                        }
                        is Bundle -> {
                            intent.putExtra(entry.key, value)
                        }
                        is Intent -> {
                            intent.putExtra(entry.key, value)
                        }
                        is ArrayList<*> -> {
                            val any = if (value.isNotEmpty()) {
                                value[0]
                            } else null
                            if (any is String) {
                                intent.putExtra(entry.key, value as ArrayList<String>)
                            } else if (any is Parcelable) {
                                intent.putExtra(entry.key, value as ArrayList<Parcelable>)
                            } else if (any is Int) {
                                intent.putExtra(entry.key, value as ArrayList<Int>)
                            } else if (any is CharSequence) {
                                intent.putExtra(entry.key, value as ArrayList<CharSequence>)
                            } else {
                                throw RuntimeException("不支持此类型 $value")
                            }
                        }
                        is Array<*> -> {
                            if (value.isArrayOf<String>()) {
                                intent.putExtra(entry.key, value as Array<String>)
                            } else if (value.isArrayOf<Parcelable>()) {
                                intent.putExtra(entry.key, value as Array<Parcelable>)
                            } else if (value.isArrayOf<CharSequence>()) {
                                intent.putExtra(entry.key, value as Array<CharSequence>)
                            } else {
                                throw RuntimeException("不支持此类型 $value")
                            }
                        }
                        else -> {
                            throw RuntimeException("不支持此类型 $value")
                        }
                    }
                }
                startActivity(intent)
            })
            // vm 可以启动界面，并携带 Bundle，接收方可调用 getBundle 获取
            mViewModel.mUiChangeLiveData.startActivityEventWithBundle?.observe(this, Observer {
                val intent = Intent(this, it?.first)
                it?.second?.let { bundle -> intent.putExtras(bundle) }
                startActivity(intent)
            })
        }
        if (isViewModelNeedStartForResult()) {
            mViewModel.mUiChangeLiveData.initStartActivityForResultEvent()

            // vm 可以启动界面
            mViewModel.mUiChangeLiveData.startActivityForResultEvent?.observe(this, Observer {
                initStartActivityForResult()
                val intent = Intent(this, it)
                mStartActivityForResult.launch(intent)
            })
            // vm 可以启动界面，并携带 Bundle，接收方可调用 getBundle 获取
            mViewModel.mUiChangeLiveData.startActivityForResultEventWithBundle?.observe(this, Observer {
                initStartActivityForResult()
                val intent = Intent(this, it?.first)
                it?.second?.let { bundle -> intent.putExtras(bundle) }
                mStartActivityForResult.launch(intent)
            })
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
     * true 则当前界面常亮
     */
    protected open fun isKeepScreenOn() = false

    /**
     * CallSuper 要求之类必须调用 super
     */
    @CallSuper
    override fun initData() {
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
}