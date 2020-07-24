package com.imyyq.mvvm.base

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable
import java.util.*

interface IArgumentsFromIntent {

    /**
     * 传递 Intent，可以在这里获取
     */
    fun getArgumentsIntent(): Intent?

    fun getBooleanArrayFromIntent(key: String?): BooleanArray? {
        return getArgumentsIntent()?.getBooleanArrayExtra(key)
    }

    fun getBooleanFromIntent(
        key: String?,
        defValue: Boolean = false
    ): Boolean {
        return getArgumentsIntent()?.getBooleanExtra(key, defValue) ?: false
    }

    fun getByteArrayFromIntent(key: String?): ByteArray? {
        return getArgumentsIntent()?.getByteArrayExtra(key)
    }

    fun getByteFromIntent(key: String?, defValue: Byte = 0): Byte {
        return getArgumentsIntent()?.getByteExtra(key, defValue) ?: 0
    }

    fun getCharArrayFromIntent(key: String?): CharArray? {
        return getArgumentsIntent()?.getCharArrayExtra(key)
    }

    fun getCharFromIntent(key: String?, defValue: Char = ' '): Char {
        return getArgumentsIntent()?.getCharExtra(key, defValue) ?: ' '
    }

    fun getCharSequenceArrayFromIntent(key: String?): Array<CharSequence?>? {
        return getArgumentsIntent()?.getCharSequenceArrayExtra(key)
    }

    fun getCharSequenceArrayListFromIntent(key: String?): ArrayList<CharSequence?>? {
        return getArgumentsIntent()?.getCharSequenceArrayListExtra(key)
    }

    fun getCharSequenceFromIntent(key: String?): CharSequence? {
        return getArgumentsIntent()?.getCharSequenceExtra(key)
    }

    fun getDoubleArrayFromIntent(key: String?): DoubleArray? {
        return getArgumentsIntent()?.getDoubleArrayExtra(key)
    }

    fun getDoubleFromIntent(key: String?, defValue: Double = 0.0): Double {
        return getArgumentsIntent()?.getDoubleExtra(key, defValue) ?: 0.0
    }

    fun getExtrasFromIntent(key: String?): Bundle? {
        return getArgumentsIntent()?.extras
    }

    fun getFloatArrayFromIntent(key: String?): FloatArray? {
        return getArgumentsIntent()?.getFloatArrayExtra(key)
    }

    fun getFloatFromIntent(key: String?, defValue: Float = 0f): Float {
        return getArgumentsIntent()?.getFloatExtra(key, defValue) ?: 0f
    }

    fun getIntArrayFromIntent(key: String?): IntArray? {
        return getArgumentsIntent()?.getIntArrayExtra(key)
    }

    fun getIntegerArrayListFromIntent(key: String?): ArrayList<Int?>? {
        return getArgumentsIntent()?.getIntegerArrayListExtra(key)
    }

    fun getIntFromIntent(key: String?, defValue: Int = 0): Int {
        return getArgumentsIntent()?.getIntExtra(key, defValue) ?: 0
    }

    fun getLongArrayFromIntent(key: String?): LongArray? {
        return getArgumentsIntent()?.getLongArrayExtra(key)
    }

    fun getLongFromIntent(key: String?, defValue: Long = 0): Long {
        return getArgumentsIntent()?.getLongExtra(key, defValue) ?: 0
    }

    /**
     * 如果传递的是多种类型的 Parcelable 数组，使用这个方法
     */
    fun getParcelableArrayFromIntent(key: String?): Array<Parcelable?>? {
        return getArgumentsIntent()?.getParcelableArrayExtra(key)
    }

    /**
     * 如果传递是一种类型的 Parcelable 数组，使用这个方法
     */
    fun <T: Parcelable> getParcelableArrayFromIntent2(key: String?): List<T?>? {
        val arr = getArgumentsIntent()?.getParcelableArrayExtra(key)
        val list = mutableListOf<T>()
        arr?.let {
            it.forEach { parcelable ->
                @Suppress("UNCHECKED_CAST")
                list.add(parcelable as T)
            }
        }
        return list
    }

    /**
     * 传递一种类型的 Parcelable 列表，使用这个方法
     */
    fun <T : Parcelable> getParcelableArrayListFromIntent(key: String?): ArrayList<T?>? {
        return getArgumentsIntent()?.getParcelableArrayListExtra(key)
    }

    fun <T : Parcelable> getParcelableFromIntent(key: String?): T? {
        return getArgumentsIntent()?.getParcelableExtra(key)
    }

    fun getSerializableFromIntent(key: String?): Serializable? {
        return getArgumentsIntent()?.getSerializableExtra(key)
    }

    fun getShortArrayFromIntent(key: String?): ShortArray? {
        return getArgumentsIntent()?.getShortArrayExtra(key)
    }

    fun getShortFromIntent(key: String?, defValue: Short = 0): Short {
        return getArgumentsIntent()?.getShortExtra(key, defValue) ?: 0
    }

    fun getStringArrayFromIntent(key: String?): Array<String?>? {
        return getArgumentsIntent()?.getStringArrayExtra(key)
    }

    fun getStringArrayListFromIntent(key: String?): ArrayList<String?>? {
        return getArgumentsIntent()?.getStringArrayListExtra(key)
    }

    fun getStringFromIntent(key: String?): String? {
        return getArgumentsIntent()?.getStringExtra(key)
    }
}