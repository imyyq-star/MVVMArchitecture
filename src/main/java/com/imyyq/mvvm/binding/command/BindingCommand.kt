package com.imyyq.mvvm.binding.command

/**
 * 执行的命令回调, 用于ViewModel与xml之间的数据绑定
 */
class BindingCommand<T> {
    private var execute: BindingAction? = null
    private var consumer: BindingConsumer<T>? = null
    private var canExecute0: BindingFunction<Boolean>? = null

    constructor(execute: BindingAction?) {
        this.execute = execute
    }

    /**
     * @param execute 带泛型参数的命令绑定
     */
    constructor(execute: BindingConsumer<T>?) {
        consumer = execute
    }

    /**
     * @param execute     触发命令
     * @param canExecute0 true则执行,反之不执行
     */
    constructor(execute: BindingAction?, canExecute0: BindingFunction<Boolean>?) {
        this.execute = execute
        this.canExecute0 = canExecute0
    }

    /**
     * @param execute     带泛型参数触发命令
     * @param canExecute0 true则执行,反之不执行
     */
    constructor(execute: BindingConsumer<T>?, canExecute0: BindingFunction<Boolean>?) {
        consumer = execute
        this.canExecute0 = canExecute0
    }

    /**
     * 执行BindingAction命令
     */
    fun execute() {
        if (execute != null && canExecute0()) {
            execute!!.call()
        }
    }

    /**
     * 执行带泛型参数的命令
     *
     * @param parameter 泛型参数
     */
    fun execute(parameter: T) {
        if (canExecute0()) {
            consumer?.call(parameter)
        }
    }

    /**
     * 是否需要执行
     *
     * @return true则执行, 反之不执行
     */
    private fun canExecute0(): Boolean {
        return if (canExecute0 == null) {
            true
        } else canExecute0!!.call()
    }
}