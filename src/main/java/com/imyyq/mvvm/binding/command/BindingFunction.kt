package com.imyyq.mvvm.binding.command

/**
 * 无参数，但是有返回值的动作
 */
interface BindingFunction<T> {
    fun call(): T
}