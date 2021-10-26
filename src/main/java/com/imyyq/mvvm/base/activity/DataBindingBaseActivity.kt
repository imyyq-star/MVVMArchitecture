package com.imyyq.mvvm.base.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import com.imyyq.mvvm.base.model.BaseModel
import com.imyyq.mvvm.base.viewmodel.BaseViewModel
import com.imyyq.mvvm.utils.getViewBinding

/**
 * DataBindingActivity 基类
 *
 * [layoutRes] 如果是 null，那么将使用反射获取 binding 实例，反射有一定的性能损耗
 * [varViewModelId] 如果是 null，说明 xml 中没有定义 [VM] 的变量，说明你不需要使用 vm，可使用 [com.imyyq.mvvm.base.NoViewModelBaseActivity]
 *
 * @author imyyq.star@gmail.com
 */
abstract class DataBindingBaseActivity<V : ViewDataBinding, VM : BaseViewModel<out BaseModel>>(
    private val varViewModelId: Int,
) : ViewBindingBaseActivity<V, VM>() {

    final override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): V {
        return getViewBinding(inflater)
    }

    @CallSuper
    override fun initViewModel() {
        super.initViewModel()
        // 绑定 v 和 vm
        mBinding.setVariable(varViewModelId, mViewModel)

        // 让 LiveData 和 xml 可以双向绑定
        mBinding.lifecycleOwner = this
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}