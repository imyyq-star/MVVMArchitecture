package com.imyyq.mvvm.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.imyyq.mvvm.utils.getViewBinding

/**
 * 具备 AppBar 功能的 ViewBinding 基类
 */
abstract class AppBarViewBindingBaseActivity<V : ViewBinding, VM : BaseViewModel<out BaseModel>,
        AppBarV : ViewBinding> : ViewBindingBaseActivity<V, VM>() {

    protected lateinit var mAppBarBinding: AppBarV

    override fun initContentView(contentView: View) {
        mAppBarBinding = initAppBarBinding(layoutInflater, null)
        super.initContentView(IAppBar.generateRootLayout(this, mAppBarBinding, contentView))
    }

    open fun initAppBarBinding(inflater: LayoutInflater, container: ViewGroup?): AppBarV =
        getViewBinding(inflater, 2)
}