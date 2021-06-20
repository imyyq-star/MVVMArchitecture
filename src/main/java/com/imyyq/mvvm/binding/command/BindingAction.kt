package com.imyyq.mvvm.binding.command

/**
 * 无参数的动作，比如点击事件不需要 v 参数时
 */
fun interface BindingAction {
    fun call()
}