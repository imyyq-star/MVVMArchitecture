package com.imyyq.mvvm.base

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.imyyq.mvvm.widget.LoadingDialog

/**
 * 通过构造函数和泛型，完成 view 的初始化和 vm 的初始化，并且将它们绑定，
 */
abstract class BaseActivity<V : ViewDataBinding, VM : BaseViewModel<*>>(
    @LayoutRes private val layoutId: Int,
    private val varViewModelId: Int? = null
) :
    AppCompatActivity(),
    IView<VM>, IDialog {

    protected lateinit var mBinding: V
    protected lateinit var mViewModel: VM

    private val mLoadingDialog: Dialog by lazy {
        LoadingDialog(this, dialogLayout())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isKeepScreenOn()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        initParam()
        initViewDataBinding()
        initUiChangeLiveData()
        initViewObservable()
        initData()
    }

    final override fun initViewDataBinding() {
        mBinding = DataBindingUtil.setContentView(this, layoutId)
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

        mBinding.unbind()
    }

    final override fun initUiChangeLiveData() {
        if (isViewModelNeedStartAndFinish()) {
            // vm 可以结束界面
            mViewModel.mUiChangeLiveData.finishEvent.observe(this, Observer { finish() })
            // vm 可以启动界面
            mViewModel.mUiChangeLiveData.startActivityEvent.observe(this, Observer {
                val intent = Intent(this, it)
                startActivity(intent)
            })
            // vm 可以启动界面，并携带 Bundle，接收方可调用 getBundle 获取
            mViewModel.mUiChangeLiveData.startActivityEventWithBundle.observe(this, Observer {
                val intent = Intent(this, it?.first)
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

    /**
     * true 则当前界面常亮
     */
    protected open fun isKeepScreenOn() = false

    override fun showDialog(msg: String?) {
        mLoadingDialog.show()
    }

    override fun dismissDialog() {
        mLoadingDialog.dismiss()
    }
}