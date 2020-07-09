package com.imyyq.mvvm.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.imyyq.mvvm.widget.CustomLayoutDialog
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir

abstract class ViewBindingBaseFragment<V : ViewBinding, VM : BaseViewModel<BaseModel>> :
    Fragment(),
    IView<V, VM>, ILoadingDialog, ILoading, IActivityResult {

    protected var mBinding: V? = null
    protected lateinit var mViewModel: VM

    private lateinit var mStartActivityForResult: ActivityResultLauncher<Intent>

    private val mLoadingDialog by lazy { CustomLayoutDialog(requireActivity(), loadingDialogLayout()) }

    private lateinit var mLoadService: LoadService<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initParam()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = initBinding(inflater, container)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewAndViewModel()
        initUiChangeLiveData()
        initViewObservable()
        initData()
    }

    @CallSuper
    override fun initViewAndViewModel() {
        mViewModel = initViewModel(this)
        // 让 vm 可以感知 v 的生命周期
        lifecycle.addObserver(mViewModel)
    }

    override fun onDestroy() {
        super.onDestroy()

        // 界面销毁时移除 vm 的生命周期感知
        lifecycle.removeObserver(mViewModel)

    }

    final override fun initUiChangeLiveData() {
        if (isViewModelNeedStartAndFinish()) {
            mViewModel.mUiChangeLiveData.initStartAndFinishEvent()

            // vm 可以结束界面
            mViewModel.mUiChangeLiveData.finishEvent?.observe(this, Observer { activity?.finish() })
            // vm 可以启动界面
            mViewModel.mUiChangeLiveData.startActivityEvent?.observe(this, Observer {
                val intent = Intent(activity, it)
                startActivity(intent)
            })
            // vm 可以启动界面，并携带 Bundle，接收方可调用 getBundle 获取
            mViewModel.mUiChangeLiveData.startActivityEventWithBundle?.observe(this, Observer {
                val intent = Intent(activity, it?.first)
                intent.putExtra(BaseViewModel.extraBundle, it?.second)
                startActivity(intent)
            })
        }
        if (isViewModelNeedStartForResult()) {
            mViewModel.mUiChangeLiveData.initStartActivityForResultEvent()

            // vm 可以启动界面
            mViewModel.mUiChangeLiveData.startActivityForResultEvent?.observe(this, Observer {
                initStartActivityForResult()
                val intent = Intent(activity, it)
                mStartActivityForResult.launch(intent)
            })
            // vm 可以启动界面，并携带 Bundle，接收方可调用 getBundle 获取
            mViewModel.mUiChangeLiveData.startActivityForResultEventWithBundle?.observe(this, Observer {
                initStartActivityForResult()
                val intent = Intent(activity, it?.first)
                intent.putExtra(BaseViewModel.extraBundle, it?.second)
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
                    val data = it.data
                    if (data != null) {
                        onActivityResult(it.resultCode, data)
                        mViewModel.onActivityResult(it.resultCode, data)
                    } else {
                        onActivityResult(it.resultCode)
                        mViewModel.onActivityResult(it.resultCode)
                    }
                }
        }
    }

    /**
     * CallSuper 要求子类必须调用 super
     */
    @CallSuper
    override fun initData() {
        // 只有目标不为空的情况才有实例化的必要
        if (getLoadSirTarget() != null) {
            mLoadService = LoadSir.getDefault().register(
                getLoadSirTarget()
            ) { v: View? -> onLoadSirReload() }

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