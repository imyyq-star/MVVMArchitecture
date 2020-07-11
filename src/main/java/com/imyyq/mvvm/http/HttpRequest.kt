package com.imyyq.mvvm.http

import android.util.ArrayMap
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.imyyq.mvvm.BuildConfig
import com.imyyq.mvvm.R
import com.imyyq.mvvm.app.GlobalConfig
import com.imyyq.mvvm.base.IBaseResponse
import com.imyyq.mvvm.http.interceptor.HeaderInterceptor
import com.imyyq.mvvm.http.interceptor.logging.Level
import com.imyyq.mvvm.http.interceptor.logging.LoggingInterceptor
import com.imyyq.mvvm.utils.AppUtil
import com.imyyq.mvvm.utils.LogUtil
import com.imyyq.mvvm.utils.SPUtils
import com.imyyq.mvvm.utils.Utils
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.platform.Platform
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    private const val mSpName = "http_request_flag"
    private const val mKeyIsSave = "is_save"

    // 缓存 service
    private val mServiceMap = ArrayMap<String, Any>()

    // 默认的 baseUrl
    lateinit var mDefaultBaseUrl: String

    // 默认的请求头
    private lateinit var mDefaultHeader: ArrayMap<String, String>

    /**
     * 存储 baseUrl，以便可以动态更改
     */
    private lateinit var mBaseUrlMap: ArrayMap<String, String>

    /**
     * 请求超时时间，秒为单位
     */
    var mDefaultTimeout = 10

    /**
     * 添加默认的请求头
     */
    @JvmStatic
    fun addDefaultHeader(name: String, value: String) {
        if (!this::mDefaultHeader.isInitialized) {
            mDefaultHeader = ArrayMap()
        }
        mDefaultHeader[name] = value
    }

    /**
     * 如果有不同的 baseURL，那么可以相同 baseURL 的接口都放在一个 Service 钟，通过此方法来获取
     */
    @JvmStatic
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

            val client = httpClientBuilder.build()
            val builder = Retrofit.Builder().client(client)
                // 基础url
                .baseUrl(host)
                // JSON解析
                .addConverterFactory(GsonConverterFactory.create())

            if (GlobalConfig.gIsNeedChangeBaseUrl) {
                if (!this::mBaseUrlMap.isInitialized) {
                    mBaseUrlMap = ArrayMap()
                }
                // 将 url 缓存起来
                val sp = SPUtils.getInstance(mSpName)
                if (sp.getBoolean(mKeyIsSave)) {
                    mBaseUrlMap[host] = sp.getString(host)
                } else {
                    mBaseUrlMap[host] = ""
                }

                builder.callFactory {
                    LogUtil.i("HttpRequest", "getService: old ${it.url()}")
                    mBaseUrlMap.forEach { entry ->
                        val key = entry.key
                        var value = entry.value
                        // 找到 url 并且需要更改
                        val url = it.url().toString()
                        if (url.startsWith(key) && value.isNotEmpty()) {
                            // 防止尾缀有问题
                            if (key.endsWith("/") && !value.endsWith("/")) {
                                value += "/"
                            } else if (!key.endsWith("/") && value.endsWith("/")) {
                                value = value.substring(0, value.length - 1)
                            }
                            // 替换 url 并创建新的 call
                            val newRequest: Request =
                                it.newBuilder()
                                    .url(HttpUrl.get(url.replaceFirst(key, value)))
                                    .build()
                            LogUtil.i("HttpRequest", "getService: new ${newRequest.url()}")
                            return@callFactory client.newCall(newRequest)
                        }
                    }
                    client.newCall(it)
                }
            }
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
    @JvmStatic
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
    @JvmStatic
    fun <T> execute(call: Call<T>): T? {
        try {
            return call.execute().body()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * Retrofit 的原生异步请求，如果你不想使用 Rx，那么可以使用这个
     */
    @JvmStatic
    fun <T, R> request(
        call: Call<T>,
        callback: CommonObserver<R>
    ): Call<T> {
        callback.onStart()

        call.enqueue(object : Callback<T> {
            override fun onResponse(
                call: Call<T>,
                response: Response<T>
            ) {
                val baseResponse = response.body()

                @Suppress("UNCHECKED_CAST")
                val resp = baseResponse as? IBaseResponse<R>
                if (resp == null) {
                    callback.onFailed(entityNullable, msgEntityNullable)
                } else {
                    callback.onNext(resp)
                }
                callback.onComplete()
            }

            override fun onFailure(
                call: Call<T>,
                t: Throwable
            ) {
                callback.onError(t)
                callback.onComplete()
            }
        })
        return call
    }

    /**
     * 在合适的位置调用此方法，多次连击后将弹出修改 baseUrl 的对话框。
     * 前提是必须开启了 [GlobalConfig.gIsNeedChangeBaseUrl] 属性，同时获取了 service 实例
     */
    fun multiClickToChangeBaseUrl(view: View, frequency: Int) {
        if (!GlobalConfig.gIsNeedChangeBaseUrl) {
            return
        }
        Utils.multiClickListener(view, frequency) {
            if (!this::mBaseUrlMap.isInitialized) {
                return@multiClickListener
            }
            AppUtil.getActivityByView(view)?.let { activity ->
                val tvList = mutableListOf<TextView>()
                val etList = mutableListOf<EditText>()

                val layout = LinearLayout(activity)
                layout.orientation = LinearLayout.VERTICAL

                mBaseUrlMap.forEach { entry ->
                    val textView = TextView(activity)
                    val edit = EditText(activity)

                    textView.text = entry.key
                    edit.setText(if (entry.value.isNotEmpty()) entry.value else entry.key)
                    edit.selectAll()

                    layout.addView(textView)
                    layout.addView(edit)

                    tvList.add(textView)
                    etList.add(edit)
                }

                val btn = Button(activity)
                btn.text = activity.getString(R.string.restore)
                btn.setOnClickListener {
                    tvList.forEachIndexed { index, textView ->
                        etList[index].setText(textView.text.toString())
                    }
                }
                layout.addView(btn)

                val sp = SPUtils.getInstance(mSpName)

                val checkBox = CheckBox(activity)
                checkBox.text = activity.getString(R.string.effective_next_time)
                checkBox.isChecked = sp.getBoolean(mKeyIsSave)
                layout.addView(checkBox)

                val editDialog = AlertDialog.Builder(activity)
                editDialog.setView(layout)

                editDialog.setPositiveButton(R.string.confirm) { dialog, _ ->
                    tvList.forEachIndexed { index, textView ->
                        mBaseUrlMap.put(textView.text.toString(), etList[index].text.toString())
                    }
                    checkBox.isChecked.apply {
                        sp.put(mKeyIsSave, this)

                        if (this) {
                            mBaseUrlMap.forEach { entry ->
                                sp.put(entry.key, entry.value)
                            }
                        } else {
                            mBaseUrlMap.forEach { entry ->
                                sp.put(entry.key, "")
                            }
                        }
                    }
                    dialog.dismiss()
                }

                editDialog.create().show()
            }
        }
    }
}
