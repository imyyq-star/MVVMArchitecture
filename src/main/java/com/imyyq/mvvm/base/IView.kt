package com.imyyq.mvvm.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewbinding.ViewBinding
import com.imyyq.mvvm.app.BaseApp
import com.imyyq.mvvm.app.GlobalConfig
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * V 层，这里的视图都是 Activity 或 Fragment
 */
interface IView<V : ViewBinding, VM : BaseViewModel<out BaseModel>>: IArgumentsFromBundle {
    /**
     * 初始化外部传进来的参数
     */
    fun initParam() {}

    /**
     * 初始化数据
     */
    fun initData() {}

    /**
     * 初始化界面观察者
     */
    fun initViewObservable() {}

    /**
     * 初始化 DataBinding，基类应该在初始化后设为 final
     */
    fun initBinding(inflater: LayoutInflater, container: ViewGroup?): V

    /**
     * 初始化视图和 VM
     */
    fun initViewAndViewModel()

    /**
     * 初始化通用的 UI 改变事件，基类应该在初始化后设为 final
     */
    fun initUiChangeLiveData()

    /**
     * 每个视图肯定会持有一个 ViewModel
     */
    @Suppress("UNCHECKED_CAST")
    fun initViewModel(viewModelStoreOwner: ViewModelStoreOwner): VM {
        var modelClass: Class<VM>?
        val type: Type? = javaClass.genericSuperclass
        modelClass = if (type is ParameterizedType) {
            type.actualTypeArguments[1] as? Class<VM>
        } else null
        if (modelClass == null) {
            modelClass = BaseViewModel::class.java as Class<VM>
        }
        //如果没有指定泛型参数，则默认使用BaseViewModel
        BaseViewModel::class.java
        val vm = ViewModelProvider(
            viewModelStoreOwner,
            ViewModelProvider.AndroidViewModelFactory(BaseApp.getInstance())
        ).get(modelClass)
        // 让 vm 也可以直接获取到 bundle
        vm.mBundle = getBundle()
        return vm
    }

    /**
     * ViewModel 是否需要启动或结束对应的 Activity，即是否有调用 [BaseViewModel.startActivity] 和 [BaseViewModel.finish]
     * 不需要的话可以设置为 false，避免创建不必要的对象
     */
    fun isViewModelNeedStartAndFinish() = GlobalConfig.gIsViewModelNeedStartAndFinish

    /**
     * ViewModel 是否需要调用 [BaseViewModel.startActivityForResult]
     * 不需要的话可以设置为 false，避免创建不必要的对象
     */
    fun isViewModelNeedStartForResult() = GlobalConfig.gIsViewModelNeedStartForResult
}