package com.imyyq.mvvm.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.imyyq.mvvm.utils.getViewBinding

/**
 * 和 [com.imyyq.mvvm.base.DataBindingBaseActivity] 类似
 */
abstract class DataBindingBaseFragment<V : ViewDataBinding, VM : BaseViewModel<out BaseModel>>(
    private val varViewModelId: Int,
    @LayoutRes private val layoutRes: Int? = null,
    sharedViewModel: Boolean = false
) : ViewBindingBaseFragment<V, VM>(sharedViewModel) {

    final override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): V {
        if (layoutRes != null) {
            return DataBindingUtil.inflate(inflater, layoutRes, container, false)
        }
        return getViewBinding(inflater, container)
    }

    override fun initViewModel() {
        super.initViewModel()
        // 绑定 v 和 vm
        if (varViewModelId != null) {
            mBinding.setVariable(varViewModelId, mViewModel)
        }

        // 让 LiveData 和 xml 可以双向绑定
        mBinding.lifecycleOwner = this
    }

    override fun onDestroyView() {
        mBinding.unbind()
        super.onDestroyView()
    }
}