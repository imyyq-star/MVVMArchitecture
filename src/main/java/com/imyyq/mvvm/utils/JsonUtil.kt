package com.imyyq.mvvm.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonUtil {
    private const val TAG = "Utils"

    val GSON = Gson()

    /**
     * 取得Json解析的结果
     *
     * @param jsonString Json格式的字符串
     * @param classT     相应的JavaBean类
     * @return 返回相应的JavaBean类实例，解析失败，返回null，是控制台上输出出错结果，不是Log
     */
    fun <T> getJsonParseResult(jsonString: String, classT: Class<T>): T? {
        var t: T? = null
        try {
            t = GSON.fromJson(jsonString, classT)
        } catch (e: Exception) {
            LogUtil.e(TAG, "###JSON解析出错，类名：" + classT.name + "###，字符串是：" + jsonString)
            e.printStackTrace()
        }

        return t
    }

    /**
     * @return JSON数组解析
     */
    fun <T> getJsonParseArrayResult(jsonString: String, classT: Class<T>): List<T>? {
        var t: List<T>? = null
        try {
            t = GSON.fromJson<List<T>>(jsonString, object : TypeToken<T>() {

            }.type)
        } catch (e: Exception) {
            LogUtil.e(TAG, "###JSON数组解析出错，类名：" + classT.name + "###，字符串是：" + jsonString)
            e.printStackTrace()
        }

        return t
    }
}
