package com.imyyq.mvvm.utils

/**
 * 判断集合是否为 null 或 empty
 */
fun <E> isEmpty(e: Collection<E>?) = e == null || e.isEmpty()
