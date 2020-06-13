package com.imyyq.mvvm.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HeaderInterceptor(private val headers: Map<String, String>) :
    Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request()
            .newBuilder()
        if (headers.isNotEmpty()) {
            for ((key, value) in headers) {
                builder.addHeader(key, value)
            }
        }
        //请求信息
        return chain.proceed(builder.build())
    }
}