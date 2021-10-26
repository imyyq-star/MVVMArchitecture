package com.imyyq.mvvm.base.viewmodel

import android.app.Application
import com.imyyq.mvvm.base.view.IAppBarProcessor
import com.imyyq.mvvm.base.model.BaseModel

/**
 * 包含标题栏处理者的 ViewModel 基类
 *
 * @author imyyq.star@gmail.com
 */
open class AppBarBaseViewModel<M : BaseModel, P : IAppBarProcessor> : BaseViewModel<M> {
    constructor(app: Application) : super(app)
    constructor(app: Application, model: M) : super(app, model)

    lateinit var mAppBarProcessor: P

    /**
     * V 层实例化 [processor] 后，会通过此方法传给 VM，这样 V 和 VM 都可以操作标题栏了
     */
    @Suppress("UNCHECKED_CAST")
    internal fun setProcessor(processor: IAppBarProcessor) {
        mAppBarProcessor = processor as P
    }
}