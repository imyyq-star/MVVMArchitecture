package com.imyyq.mvvm.base

import android.os.Bundle

interface IArguments {
    /**
     * 传递 bundle，在这里可以获取
     */
    fun getBundle(): Bundle?

    fun getStringFromBundle(key: String?, defValue: String? = null): String? {
        return getBundle()?.getString(key, defValue)
    }

    fun getIntFromBundle(key: String?, defValue: Int = 0): Int? {
        return getBundle()?.getInt(key, defValue)
    }

    fun getBooleanFromBundle(key: String?, defValue: Boolean = false): Boolean? {
        return getBundle()?.getBoolean(key, defValue)
    }

    fun getLongFromBundle(key: String?, defValue: Long = 0): Long? {
        return getBundle()?.getLong(key, defValue)
    }

    fun getDoubleFromBundle(key: String?, defValue: Double = 0.0): Double? {
        return getBundle()?.getDouble(key, defValue)
    }

    fun getByteFromBundle(key: String?, defValue: Byte = 0): Byte? {
        return getBundle()?.getByte(key, defValue)
    }

    fun getShortFromBundle(key: String?, defValue: Short = 0): Short? {
        return getBundle()?.getShort(key, defValue)
    }

    fun getFloatFromBundle(key: String?, defValue: Float = 0f): Float? {
        return getBundle()?.getFloat(key, defValue)
    }
}