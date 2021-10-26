package com.imyyq.mvvm.utils

import androidx.annotation.IntRange

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

/**
 * 从 4 个 byte，32 位的 int 中取一个 byte，位是左高右低的
 *
 * @param byteIndex     要取哪一个 byte，按照左高右低是 3-0，即 0 是取最低位，3 是取最高位
 * @return 返回 byte
 */
fun Int.getByte(@IntRange(from = 0, to = 3) byteIndex: Int): Byte {
    return (this shr byteIndex * 8).toByte()
}