package com.imyyq.mvvm.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.imyyq.mvvm.base.model.BaseModel
import com.imyyq.mvvm.base.view.IAppBar
import com.imyyq.mvvm.base.viewmodel.BaseViewModel
import com.imyyq.mvvm.utils.Utils
import com.imyyq.mvvm.utils.getViewBinding

/**
 * 类似 [com.imyyq.mvvm.base.AppBarViewBindingBaseActivity]
 *
 * @author imyyq.star@gmail.com
 */
abstract class AppBarViewBindingBaseFragment<V : ViewBinding, VM : BaseViewModel<out BaseModel>,
        AppBarV : ViewBinding> : ViewBindingBaseFragment<V, VM>() {

    protected lateinit var mAppBarBinding: AppBarV

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = initBinding(inflater, container)
        mAppBarBinding = initAppBarBinding(layoutInflater, null)
        return IAppBar.generateRootLayout(requireActivity(), mAppBarBinding, mBinding.root)
    }

    open fun initAppBarBinding(inflater: LayoutInflater, container: ViewGroup?): AppBarV =
        getViewBinding(inflater, container, 2)

    override fun onDestroyView() {
        super.onDestroyView()

        Utils.releaseBinding(this.javaClass, AppBarViewBindingBaseFragment::class.java, this, "mAppBarBinding")
    }
}