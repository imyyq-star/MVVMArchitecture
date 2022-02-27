package com.imyyq.mvvm.binding.command

/**
 *
 * @author imyyq.star@gmail.com
 */
fun interface BindingFunction<T, R> {
    fun apply(t: T): R
}