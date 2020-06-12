package com.imyyq.mvvm.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

abstract class BaseFragment<V : ViewDataBinding, VM : BaseViewModel<BaseModel>>(
    @LayoutRes val layoutId: Int,
    private val varViewModelId: Int? = null
) :
    Fragment(),
    IView<VM>, IDialog {

    protected lateinit var mBinding: V
    protected lateinit var mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initParam()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            layoutId,
            container,
            false
        )
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewDataBinding()
        initUiChangeLiveData()
        initViewObservable()
        initData()
    }

    final override fun initViewDataBinding() {
        mViewModel = initViewModel(this)
        // 绑定 v 和 vm
        if (varViewModelId != null) {
            mBinding.setVariable(varViewModelId, mViewModel)
        }

        // 让 LiveData 和 xml 可以双向绑定
        mBinding.lifecycleOwner = this
        // 让 vm 可以感知 v 的生命周期
        lifecycle.addObserver(mViewModel)
    }

    override fun onDestroy() {
        super.onDestroy()

        // 界面销毁时移除 vm 的生命周期感知
        lifecycle.removeObserver(mViewModel)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.unbind()
    }

    final override fun initUiChangeLiveData() {
        if (isViewModelNeedStartAndFinish()) {
            // vm 可以结束界面
            mViewModel.mUiChangeLiveData.finishEvent.observe(this, Observer { activity?.finish() })
            // vm 可以启动界面
            mViewModel.mUiChangeLiveData.startActivityEvent.observe(this, Observer {
                val intent = Intent(activity, it)
                startActivity(intent)
            })
            // vm 可以启动界面，并携带 Bundle，接收方可调用 getBundle 获取
            mViewModel.mUiChangeLiveData.startActivityEventWithBundle.observe(this, Observer {
                val intent = Intent(activity, it?.first)
                intent.putExtra(BaseViewModel.extraBundle, it?.second)
                startActivity(intent)
            })
        }

        if (isNeedDialog()) {
            // 显示对话框
            mViewModel.mUiChangeLiveData.showDialogEvent.observe(this, Observer {
                showDialog(it)
            })
            // 隐藏对话框
            mViewModel.mUiChangeLiveData.dismissDialogEvent.observe(this, Observer {
                dismissDialog()
            })
        }
    }
}