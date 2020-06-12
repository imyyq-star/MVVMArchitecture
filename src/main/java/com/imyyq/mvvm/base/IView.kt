package com.imyyq.mvvm.base

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.imyyq.mvvm.app.BaseApp
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * V 层，这里的视图都是 Activity 或 Fragment
 */
interface IView<VM : BaseViewModel<*>> {
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
    fun initViewDataBinding()

    /**
     * 初始化通用的 UI 改变事件，基类应该在初始化后设为 final
     */
    fun initUiChangeLiveData()

    /**
     * 每个视图肯定会持有一个 ViewModel
     */
    @Suppress("UNCHECKED_CAST")
    fun initViewModel(viewModelStoreOwner: ViewModelStoreOwner): VM {
        val modelClass: Class<VM>
        val type: Type? = javaClass.genericSuperclass
        modelClass = (if (type is ParameterizedType) {
            type.actualTypeArguments[1] as Class<VM>
        } else {
            //如果没有指定泛型参数，则默认使用BaseViewModel
            BaseViewModel::class.java
        }) as Class<VM>
        return ViewModelProvider(
            viewModelStoreOwner,
            ViewModelProvider.AndroidViewModelFactory(BaseApp.getInstance())
        ).get(modelClass)
    }

    fun getBundle(activity: Activity): Bundle? {
        return activity.intent.getBundleExtra(BaseViewModel.extraBundle)
    }
}