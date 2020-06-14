package com.imyyq.mvvm.base

open class MultiItemViewModel<VM : BaseViewModel<*>>(
    viewModel: VM,
    position: Int? = null,
    var mItemType: Any? = null
) :
    ItemViewModel<VM>(viewModel, position)