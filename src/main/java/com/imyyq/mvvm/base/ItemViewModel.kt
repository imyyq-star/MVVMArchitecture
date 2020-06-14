package com.imyyq.mvvm.base

open class ItemViewModel<VM : BaseViewModel<*>>(
    protected var mViewModel: VM,
    // 有需要的话，可以设置当前实例对应的 position
    protected var mPosition: Int? = null
)