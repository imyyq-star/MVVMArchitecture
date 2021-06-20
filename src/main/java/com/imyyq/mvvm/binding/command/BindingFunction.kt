package com.imyyq.mvvm.binding.command

fun interface BindingFunction<T, R> {
    fun apply(t: T): R
}