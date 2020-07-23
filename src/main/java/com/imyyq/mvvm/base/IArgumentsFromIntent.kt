package com.imyyq.mvvm.base

import android.content.Intent
import android.os.Parcelable
import java.io.Serializable

interface IArgumentsFromIntent {

    /**
     * 传递 Intent，可以在这里获取
     */
    fun getArgumentsIntent(): Intent?

    fun getBooleanFromIntent(key: String, defValue: Boolean = false): Boolean? =
        getArgumentsIntent()?.getBooleanExtra(key, defValue)

    fun getByteFromIntent(key: String, defValue: Byte = 0): Byte? =
        getArgumentsIntent()?.getByteExtra(key, defValue)

    fun getCharFromIntent(key: String, defValue: Char = ' '): Char? =
        getArgumentsIntent()?.getCharExtra(key, defValue)

    fun getShortFromIntent(key: String, defValue: Short = 0): Short? =
        getArgumentsIntent()?.getShortExtra(key, defValue)

    fun getIntFromIntent(key: String, defValue: Int = 0): Int? =
        getArgumentsIntent()?.getIntExtra(key, defValue)

    fun getLongFromIntent(key: String, defValue: Long = 0): Long? =
        getArgumentsIntent()?.getLongExtra(key, defValue)

    fun getFloatFromIntent(key: String, defValue: Float = 0F): Float? =
        getArgumentsIntent()?.getFloatExtra(key, defValue)

    fun getDoubleFromIntent(key: String, defValue: Double = 0.0): Double? =
        getArgumentsIntent()?.getDoubleExtra(key, defValue)

    fun getStringFromIntent(key: String): String? = getArgumentsIntent()?.getStringExtra(key)

    fun <T : Parcelable> getParcelableFromIntent(key: String): T? =
        getArgumentsIntent()?.getParcelableExtra(key)

    fun getSerializableFromIntent(key: String): Serializable? =
        getArgumentsIntent()?.getSerializableExtra(key)

    fun getBooleanArrayFromIntent(key: String): BooleanArray? =
        getArgumentsIntent()?.getBooleanArrayExtra(key)

    fun getByteArrayFromIntent(key: String): ByteArray? =
        getArgumentsIntent()?.getByteArrayExtra(key)

    fun getShortArrayFromIntent(key: String): ShortArray? =
        getArgumentsIntent()?.getShortArrayExtra(key)

    fun getCharArrayFromIntent(key: String): CharArray? =
        getArgumentsIntent()?.getCharArrayExtra(key)

    fun getIntArrayFromIntent(key: String): IntArray? =
        getArgumentsIntent()?.getIntArrayExtra(key)

    fun getLongArrayFromIntent(key: String): LongArray? =
        getArgumentsIntent()?.getLongArrayExtra(key)

    fun getFloatArrayFromIntent(key: String): FloatArray? =
        getArgumentsIntent()?.getFloatArrayExtra(key)

    fun getDoubleArrayFromIntent(key: String): DoubleArray? =
        getArgumentsIntent()?.getDoubleArrayExtra(key)

    fun getStringArrayFromIntent(key: String): Array<String>? =
        getArgumentsIntent()?.getStringArrayExtra(key)

    fun getParcelableArrayFromIntent(key: String): Array<Parcelable>? =
        getArgumentsIntent()?.getParcelableArrayExtra(key)

}