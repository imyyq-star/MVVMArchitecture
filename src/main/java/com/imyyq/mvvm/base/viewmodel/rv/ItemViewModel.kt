package com.imyyq.mvvm.base.viewmodel.rv

import com.imyyq.mvvm.base.model.BaseModel
import com.imyyq.mvvm.base.viewmodel.BaseViewModel

/**
 * 列表 item 数据基类，持有了页面的 vm，这样可以操作到整个列表数据。
 * 如果有必要，可以持有当前数据对应的列表索引
 *
 * @author imyyq.star@gmail.com
 */
open class ItemViewModel<VM : BaseViewModel<out BaseModel>>(
    var mViewModel: VM,
    // 有需要的话，可以设置当前实例对应的 position
    var mPosition: Int? = null
)