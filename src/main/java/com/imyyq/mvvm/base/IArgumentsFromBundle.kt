package com.imyyq.mvvm.base

import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import android.util.SparseArray
import java.io.Serializable
import java.util.*

interface IArgumentsFromBundle {
    /**
     * 传递 bundle，在这里可以获取
     */
    fun getBundle(): Bundle?

    fun getByteFromBundle(key: String?, defValue: Byte = 0): Byte {
        return getBundle()?.getByte(key, defValue) ?: 0
    }

    fun getCharFromBundle(key: String?, defValue: Char = ' '): Char {
        return getBundle()?.getChar(key, defValue) ?: ' '
    }

    fun getShortFromBundle(key: String?, defValue: Short = 0): Short {
        return getBundle()?.getShort(key, defValue) ?: 0
    }

    fun getFloatFromBundle(key: String?, defValue: Float = 0f): Float {
        return getBundle()?.getFloat(key, defValue) ?: 0f
    }

    fun getCharSequenceFromBundle(
        key: String?,
        defValue: CharSequence? = null
    ): CharSequence? {
        return getBundle()?.getCharSequence(key, defValue)
    }

    fun getSizeFromBundle(key: String?): Size? {
        return getBundle()?.getSize(key)
    }

    fun getSizeFFromBundle(key: String?): SizeF? {
        return getBundle()?.getSizeF(key)
    }

    fun getBundleFromBundle(key: String?): Bundle? {
        return getBundle()?.getBundle(key)
    }

    fun <T : Parcelable> getParcelableFromBundle(key: String?): T? {
        return getBundle()?.getParcelable(key)
    }

    fun getParcelableArrayFromBundle(key: String?): Array<Parcelable?>? {
        return getBundle()?.getParcelableArray(key)
    }

    fun <T : Parcelable> getParcelableArrayListFromBundle(key: String?): ArrayList<T>? {
        return getBundle()?.getParcelableArrayList(key)
    }

    fun <T : Parcelable?> getSparseParcelableArrayFromBundle(key: String?): SparseArray<T>? {
        return getBundle()?.getSparseParcelableArray(key)
    }

    fun getSerializableFromBundle(key: String?): Serializable? {
        return getBundle()?.getSerializable(key)
    }

    fun getIntegerArrayListFromBundle(key: String?): ArrayList<Int?>? {
        return getBundle()?.getIntegerArrayList(key)
    }

    fun getStringArrayListFromBundle(key: String?): ArrayList<String?>? {
        return getBundle()?.getStringArrayList(key)
    }

    fun getCharSequenceArrayListFromBundle(key: String?): ArrayList<CharSequence?>? {
        return getBundle()?.getCharSequenceArrayList(key)
    }

    fun getByteArrayFromBundle(key: String?): ByteArray? {
        return getBundle()?.getByteArray(key)
    }

    fun getShortArrayFromBundle(key: String?): ShortArray? {
        return getBundle()?.getShortArray(key)
    }

    fun getCharArrayFromBundle(key: String?): CharArray? {
        return getBundle()?.getCharArray(key)
    }

    fun getFloatArrayFromBundle(key: String?): FloatArray? {
        return getBundle()?.getFloatArray(key)
    }

    fun getCharSequenceArrayFromBundle(key: String?): Array<CharSequence?>? {
        return getBundle()?.getCharSequenceArray(key)
    }

    fun getBinderFromBundle(key: String?): IBinder? {
        return getBundle()?.getBinder(key)
    }

    fun getBooleanFromBundle(key: String?, defValue: Boolean = false): Boolean? {
        return getBundle()?.getBoolean(key, defValue)
    }

    fun getIntFromBundle(key: String?, defValue: Int = 0): Int {
        return getBundle()?.getInt(key, defValue) ?: 0
    }

    fun getLongFromBundle(key: String?, defValue: Long = 0): Long {
        return getBundle()?.getLong(key, defValue) ?: 0
    }

    fun getDoubleFromBundle(key: String?, defValue: Double = 0.0): Double {
        return getBundle()?.getDouble(key, defValue) ?: 0.0
    }

    fun getStringFromBundle(key: String?, defValue: String? = null): String? {
        return getBundle()?.getString(key, defValue)
    }

    fun getBooleanArrayFromBundle(key: String?): BooleanArray? {
        return getBundle()?.getBooleanArray(key)
    }

    fun getIntArrayFromBundle(key: String?): IntArray? {
        return getBundle()?.getIntArray(key)
    }

    fun getLongArrayFromBundle(key: String?): LongArray? {
        return getBundle()?.getLongArray(key)
    }

    fun getDoubleArrayFromBundle(key: String?): DoubleArray? {
        return getBundle()?.getDoubleArray(key)
    }

    fun getStringArrayFromBundle(key: String?): Array<String?>? {
        return getBundle()?.getStringArray(key)
    }
}