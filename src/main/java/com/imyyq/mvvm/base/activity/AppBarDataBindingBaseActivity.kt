package com.imyyq.mvvm.base.activity

import android.view.View
import androidx.databinding.ViewDataBinding
import com.imyyq.mvvm.base.model.BaseModel
import com.imyyq.mvvm.base.view.IAppBar
import com.imyyq.mvvm.base.view.IAppBarProcessor
import com.imyyq.mvvm.base.viewmodel.AppBarBaseViewModel

/**
 * 包含 AppBar 的 DataBinding 基类
 * [varAppBarProcessorId] 既然使用了 dataBinding，那么必须有个处理者去关联 xml，即 AppBar 的处理者
 * [appBarLayoutRes] AppBar 的 xml 资源，为空表示根据 [V] 反射获取
 *
 * @author imyyq.star@gmail.com
 */
abstract class AppBarDataBindingBaseActivity<V : ViewDataBinding, VM : AppBarBaseViewModel<out BaseModel, out IAppBarProcessor>,
        AppBarV : ViewDataBinding, AppBarP : IAppBarProcessor>(
    varViewModelId: Int,
    private val varAppBarProcessorId: Int,
) : DataBindingBaseActivity<V, VM>(varViewModelId), IAppBar<AppBarP> {

    protected lateinit var mAppBarBinding: AppBarV
    protected lateinit var mAppBarProcessor: AppBarP

    override fun initContentView(contentView: View) {
        val pair: Pair<AppBarV, View> = IAppBar.inflateRootLayout(this, contentView)
        mAppBarBinding = pair.first
        super.initContentView(pair.second)
    }

    override fun initViewModel() {
        super.initViewModel()
        // 让 vm 层也可以访问到标题栏处理者
        mAppBarProcessor = initAppBarProcessor()
        mAppBarBinding.setVariable(varAppBarProcessorId, mAppBarProcessor)
        mAppBarBinding.lifecycleOwner = this

        mViewModel.setProcessor(mAppBarProcessor)
    }

    override fun onDestroy() {
        super.onDestroy()
        mAppBarBinding.unbind()
    }
}