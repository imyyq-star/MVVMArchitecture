package com.imyyq.mvvm.base

/**
 * 列表 item 数据基类，持有了页面的 vm，这样可以操作到整个列表数据。
 * 如果有必要，可以持有当前数据对应的列表索引
 */
open class ItemViewModel<VM : BaseViewModel<*>>(
    protected var mViewModel: VM,
    // 有需要的话，可以设置当前实例对应的 position
    protected var mPosition: Int? = null
)