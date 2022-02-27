package com.imyyq.mvvm.binding.command

/**
 * 一个参数的动作，比如点击事件的 v 参数
 *
 * @author imyyq.star@gmail.com
 */
fun interface BindingConsumer<T> {
    fun call(t: T)
}