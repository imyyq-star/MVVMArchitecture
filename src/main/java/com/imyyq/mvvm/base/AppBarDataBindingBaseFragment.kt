package com.imyyq.mvvm.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.imyyq.mvvm.utils.Utils

/**
 * 类似 [com.imyyq.mvvm.base.AppBarDataBindingBaseActivity]
 */
abstract class AppBarDataBindingBaseFragment<V : ViewDataBinding, VM : AppBarBaseViewModel<out BaseModel, out IAppBarProcessor>,
        AppBarV : ViewDataBinding, AppBarP : IAppBarProcessor>(
    varViewModelId: Int,
    private val varAppBarProcessorId: Int, // 既然使用了 dataBinding，那么必须有个处理者去关联 xml
    @LayoutRes layoutRes: Int? = null,
    @LayoutRes private val appBarLayoutRes: Int? = null,
    sharedViewModel: Boolean = false
) : DataBindingBaseFragment<V, VM>(varViewModelId, layoutRes, sharedViewModel), IAppBar<AppBarP> {

    protected lateinit var mAppBarBinding: AppBarV
    protected lateinit var mAppBarProcessor: AppBarP

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = initBinding(inflater, container)

        val pair: Pair<AppBarV, View> =
            IAppBar.inflateRootLayout(this, mBinding.root, appBarLayoutRes)
        mAppBarBinding = pair.first
        return pair.second
    }

    override fun initViewModel() {
        super.initViewModel()
        // 让 vm 层也可以访问到标题栏处理者
        mAppBarProcessor = initAppBarProcessor()
        mAppBarBinding.setVariable(varAppBarProcessorId, mAppBarProcessor)
        mAppBarBinding.lifecycleOwner = this

        mViewModel.setProcessor(mAppBarProcessor)
    }

    override fun onDestroyView() {
        mAppBarBinding.unbind()

        super.onDestroyView()

        Utils.releaseBinding(this.javaClass, AppBarDataBindingBaseFragment::class.java, this, "mAppBarBinding")
    }
}