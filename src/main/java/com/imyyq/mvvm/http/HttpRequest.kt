package com.imyyq.mvvm.http

import android.util.ArrayMap
import com.imyyq.mvvm.BuildConfig
import com.imyyq.mvvm.http.interceptor.HeaderInterceptor
import com.imyyq.mvvm.http.interceptor.logging.Level
import com.imyyq.mvvm.http.interceptor.logging.LoggingInterceptor
import com.imyyq.mvvm.utils.AppUtil
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by 杨永青 on 16/9/11.
 *
 *
 *
 *
 * 目的1：没网的时候，尝试读取缓存，避免界面空白，只需要addInterceptor和cache即可（已实现）
 * 目的2：有网的时候，总是读取网络上最新的，或者设置一定的超时时间，比如10秒内有多个同一请求，则都从缓存中获取（没实现）
 * 目的3：不同的接口，不同的缓存策略（？）
 */
object HttpRequest {

    // 缓存 service
    private val mServiceMap = ArrayMap<String, Any>()

    // 默认的 baseUrl
    lateinit var mDefaultBaseUrl: String

    // 默认的请求头
    private lateinit var mDefaultHeader: ArrayMap<String, String>

    /**
     * 请求超时时间，秒为单位
     */
    var mDefaultTimeout = 10

    /**
     * 添加默认的请求头
     */
    fun addDefaultHeader(name: String, value: String) {
        if (!this::mDefaultHeader.isInitialized) {
            mDefaultHeader = ArrayMap()
        }
        mDefaultHeader[name] = value
    }

    /**
     * 如果有不同的 baseURL，那么可以相同 baseURL 的接口都放在一个 Service 钟，通过此方法来获取
     */
    fun <T> getService(cls: Class<T>, host: String, vararg interceptors: Interceptor?): T {
        val name = cls.name

        var obj: Any? = mServiceMap[name]
        if (obj == null) {
            val httpClientBuilder = OkHttpClient.Builder()

            // 超时时间
            httpClientBuilder.connectTimeout(mDefaultTimeout.toLong(), TimeUnit.SECONDS)

            // 拦截器
            interceptors.forEach { interceptor ->
                interceptor?.let {
                    httpClientBuilder.addInterceptor(it)
                }
            }

            // 日志拦截器，只在 debug 期间生效
            if (BuildConfig.DEBUG) {
                httpClientBuilder
                    .addInterceptor(
                        LoggingInterceptor
                            .Builder()
                            // 是否开启日志打印
                            .loggable(BuildConfig.DEBUG)
                            // 打印的等级
                            .setLevel(Level.BASIC)
                            // 打印类型
                            .log(Platform.INFO)
                            // request 的 Tag
                            .request("Request")
                            // Response 的 Tag
                            .response("Response")
                            .build()
                    )
            }

            val builder = Retrofit.Builder().client(httpClientBuilder.build())
                // 基础url
                .baseUrl(host)
                // JSON解析
                .addConverterFactory(GsonConverterFactory.create())
            if (AppUtil.isRetrofitUseRx) {
                // Kotlin 使用协程，Java 使用 rx
                builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 回调处理，可以设置Rx作为回调的处理
            }
            obj = builder.build().create(cls)
            mServiceMap[name] = obj
        }
        @Suppress("UNCHECKED_CAST")
        return obj as T
    }

    /**
     * 设置了 [mDefaultBaseUrl] 后，可通过此方法获取 Service
     */
    fun <T> getService(cls: Class<T>): T {
        if (!this::mDefaultBaseUrl.isInitialized) {
            throw RuntimeException("必须初始化 mBaseUrl")
        }
        if (this::mDefaultHeader.isInitialized) {
            val headers = HeaderInterceptor(mDefaultHeader)
            return getService(cls, mDefaultBaseUrl, headers)
        }
        return getService(cls, mDefaultBaseUrl, null)
    }

    /**
     * 同步的请求，当一个界面需要调用多个接口才能呈现出来时，可以在子线程中或者Observable.zip操作多个接口
     */
    fun <T> execute(call: Call<T>): T? {
        try {
            return call.execute().body()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}
