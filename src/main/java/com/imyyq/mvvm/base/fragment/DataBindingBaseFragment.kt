package com.imyyq.mvvm.base.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.imyyq.mvvm.base.model.BaseModel
import com.imyyq.mvvm.base.viewmodel.BaseViewModel
import com.imyyq.mvvm.utils.getViewBinding

/**
 * 和 [com.imyyq.mvvm.base.DataBindingBaseActivity] 类似
 *
 * @author imyyq.star@gmail.com
 */
abstract class DataBindingBaseFragment<V : ViewDataBinding, VM : BaseViewModel<out BaseModel>>(
    private val varViewModelId: Int,
    sharedViewModel: Boolean = false
) : ViewBindingBaseFragment<V, VM>(sharedViewModel) {

    final override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): V {
        return getViewBinding(inflater, container)
    }

    override fun initViewModel() {
        super.initViewModel()
        // 绑定 v 和 vm
        mBinding.setVariable(varViewModelId, mViewModel)

        // 让 LiveData 和 xml 可以双向绑定
        mBinding.lifecycleOwner = this
    }

    override fun onDestroyView() {
        mBinding.unbind()
        super.onDestroyView()
    }
}