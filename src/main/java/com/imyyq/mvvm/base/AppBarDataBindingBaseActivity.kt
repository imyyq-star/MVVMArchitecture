package com.imyyq.mvvm.base

import android.view.View
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.imyyq.mvvm.app.GlobalConfig

abstract class AppBarDataBindingBaseActivity<V : ViewDataBinding, VM : AppBarBaseViewModel<out BaseModel, out IAppBarProcessor>,
        AppBarV : ViewDataBinding, AppBarP : IAppBarProcessor>(
    @LayoutRes layoutId: Int,
    varViewModelId: Int? = null,
    private val varAppBarProcessorId: Int, // 既然使用了 dataBinding，那么必须有个处理者去关联 xml
    @LayoutRes private val appBarLayoutId: Int? = GlobalConfig.AppBar.gAppBarLayoutId // 可以全局设置，也可以单独设置
) : DataBindingBaseActivity<V, VM>(layoutId, varViewModelId), IAppBar<AppBarP> {

    protected lateinit var mAppBarBinding: AppBarV
    protected lateinit var mAppBarProcessor: AppBarP

    override fun initContentView(contentView: View) {
        appBarLayoutId?.let {
            val pair: Pair<AppBarV, LinearLayout> = IAppBar.inflateRootLayout(this, contentView, it)
            mAppBarBinding = pair.first
            super.initContentView(pair.second)
        }
    }

    override fun initViewModel() {
        super.initViewModel()
        // 让 vm 层也可以访问到标题栏处理者
        if (appBarLayoutId != null) {
            // 实例化标题栏的处理者
            mAppBarProcessor = initAppBarProcessor()
            mAppBarBinding.setVariable(varAppBarProcessorId, mAppBarProcessor)
            mAppBarBinding.lifecycleOwner = this

            mViewModel.setProcessor(mAppBarProcessor)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mAppBarBinding.isInitialized) {
            mAppBarBinding.unbind()
        }
    }
}