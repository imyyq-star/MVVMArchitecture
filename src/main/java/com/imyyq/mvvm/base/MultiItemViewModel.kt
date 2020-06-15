package com.imyyq.mvvm.base

open class MultiItemViewModel<VM : BaseViewModel<*>>(
    viewModel: VM,
    var mItemType: Any? = null,
    position: Int? = null
) :
    ItemViewModel<VM>(viewModel, position)