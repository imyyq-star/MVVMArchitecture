package com.imyyq.mvvm.utils

/**
 * 位运算
 */

/**
 * 当前值是否包含 [flag]
 */
fun Int.containFlag(flag: Int) = this and flag != 0

/**
 * 移除 flag
 */
fun Int.removeFlag(flag: Int) = this and flag.inv()