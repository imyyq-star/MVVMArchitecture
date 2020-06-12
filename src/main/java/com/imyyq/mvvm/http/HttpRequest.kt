package com.imyyq.mvvm.http

import android.util.ArrayMap
import com.imyyq.mvvm.utils.FileUtil
import com.imyyq.mvvm.utils.NetworkUtil
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
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

    private val mServiceMap = ArrayMap<String, Any>()

    private var mHost: String? = null

    var staticHeader: ArrayMap<String, String>? = null

    /**
     * 请求超时时间
     */
    private val DEFAULT_TIMEOUT = 10

    fun setHost(mHost: String) {
        HttpRequest.mHost = mHost
    }

    fun addHeader(name: String, value: String) {
        if (staticHeader == null) {
            staticHeader = ArrayMap()
        }
        staticHeader!![name] = value
    }

    @Synchronized
    fun <T> getService(cls: Class<T>, host: String): T {
        val name = cls.name

        var obj: Any? = mServiceMap[name]
        if (obj == null) {
            val httpClientBuilder = OkHttpClient.Builder()
            // 超时时间
            httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)

            // 设置缓存目录
            val cache = Cache(
                File(FileUtil.cacheDir, "httpCache"),
                (1024 * 1024 * 100).toLong()
            )
            httpClientBuilder.cache(cache)

            /*
             *  拦截器，有没有网络都会响应，这里必须设置没网则访问缓存，不然不生效
             */
            httpClientBuilder.addInterceptor { chain ->
                var request = chain.request()

                // 设置请求头，需要请求头的基本每个接口都需要
                if (staticHeader != null && !staticHeader!!.isEmpty()) {
                    val builder = request.newBuilder()
                    for ((key, value) in staticHeader!!) {
                        builder.header(key, value)
                    }
                    builder.method(request.method(), request.body())
                    request = builder.build()
                }

                // 判断网络   没网读取缓存
                if (!NetworkUtil.isConnected()) {
                    request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
                }
                chain.proceed(request)
            }

            // 有网络才会调用
            //            httpClientBuilder.addNetworkInterceptor(chain -> {
            //                Request request = chain.request();
            //                okhttp3.Response response = chain.proceed(request);
            //
            //                Log.i("HttpRequest", "addNetworkInterceptor: ");
            //                if (NetworkUtil.isConnected())
            //                {
            //                    int maxAge = 10;
            //                    // 有网络时 设置缓存超时时间
            //                    response.newBuilder().removeHeader("Pragma").header("Cache-Control",
            //                            "public, max-age=" + maxAge).build();
            //                }
            //                return response;
            //            });

            obj = Retrofit.Builder().client(httpClientBuilder.build()).baseUrl(host) // 基础url
                .addConverterFactory(GsonConverterFactory.create()) // JSON解析
                // 使用协程，不使用 rx
                //.addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 回调处理，可以设置Rx作为回调的处理
                .build() // 构建
                .create(cls)
            mServiceMap[name] = obj
        }
        return obj as T
    }

    fun <T> getService(cls: Class<T>): T {
        if (mHost == null) {
            throw RuntimeException("必须初始化mHost")
        }
        return getService(cls, mHost ?: "")
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
