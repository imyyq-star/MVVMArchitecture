# 列表绑定
这部分采用第三方库 [binding-collection-adapter](https://github.com/evant/binding-collection-adapter)，在配置文件中开启 bindingCollection 相关的配置即可，具体用法详见此库的用法。
框架了提供了 ItemViewModel 和 MultiItemViewModel 这两个列表子项的基类，沿用了 MVVMHabit 。当然，如果你不想要这样的方式，自然也可以使用 [BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper) 这类的 Adapter 适配器库，只不过就需要多写点代码。

# 图片加载
图片加载使用 Glide，使用 DataBinding 可以很方便的绑定图片地址和占位图：

```xml
<ImageView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    url="@{viewModel.mImageUrl}"
    placeholderRes="@{R.mipmap.ic_launcher}"
/>
```

具体如何实现的，详见 com.imyyq.mvvm.binding.viewadapter.image 包下的代码。其实 DataBinding 对于 View 属性相关的绑定，自定义的绑定是不需要加前缀的，如上面的 url 绑定。

而且如果 View 有定义 setXXXX 方法，那么使用 DataBinding 就会有 app:xXXX 属性：

```xml
<ImageView
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:visibility="@{View.GONE}"
/>
```


# 网络请求
以前我们还是用的是 RxJava 配合 Retrofit，现在，我们可以直接使用协程了，不得不说节省了很多代码，而且加上 viewModel 的 ktx，可让请求和生命周期相关联，在生命周期结束时，取消掉请求，以免造成资源浪费。

```kotlin
@GET("friend/json")
suspend fun login(
    @Query("userName") userName: String,
    @Query("pwd") pwd: String
): BaseEntity<List<FriendWebSiteEntity?>?>?
```

在 ViewModel 中使用协程：

```kotlin
// 使用 vm 的协程，可以在界面销毁时自动取消该协程
viewModelScope.launch {
    val entity = mModel.login("userName", "pwd")
    ...
}
```

上面的代码不会阻塞主线程，这里我有疑惑，但目前还没有答案，调用的 Retrofit 的时候，最好是在 IO 中执行，如下：

```kotlin
// 使用 vm 的协程，可以在界面销毁时自动取消该协程
viewModelScope.launch {
    withContext(Dispatchers.IO) {
        val entity = mModel.login("userName", "pwd")
    }
    ...
}
```

获取 Service，添加请求头等都在 HttpRequest 类中，如果是 debug 阶段，那么会增加日志拦截器，把请求和响应都打在 Logcat 上

以上只是简单的列了下用法，让大家看看方便程度，老是写 viewModelScope 或 withContext，这种重复代码挺没意思，参考了 [MVVMLin](https://github.com/AleynP/MVVMLin) 这个开源库后，我封装了类似的代码，详细的看下面关于请求结果的介绍。

## 关于请求结果处理的问题
通常来说，服务端返回的数据格式都是 JSON，有些很老的接口可能还是 XML，有追求的团队还可能采用 ProtocolBuffer，解析的工作 Retrofit 已经帮我们做好了。
我们要做的就是处理解析后的实体对象，一般来说，正常的后端接口都有统一格式的，比如如下：

```json
{
  "errorCode": 0,
  "errorMsg": "",
  "data": []
}
```

至少会有以上最基本的字段，**以上字段名称其实极大可能不同团队是不一样的，甚至一个应用中接口的格式不统一我也是遇到过的**，因此参考了 [MVVMLin](https://github.com/AleynP/MVVMLin)，我们提供一个接口：

```kotlin
/**
 * 实体类必须实现这个接口并复写其中的方法，这样才可以使用 BaseViewModel 中的协程方法
 */
interface IBaseResponse<T> {
    fun code(): Int?
    fun msg(): String?
    fun data(): T?
    fun isSuccess(): Boolean
}
```

然后无论我们的接口字段是怎么样的，就都能统一了，比如上述的接口字段，我们将它封装成一个基类：

```kotlin
data class BaseEntity<T>(
    var data: T?,
    var errorCode: Int?,
    var errorMsg: String?
) : IBaseResponse<T> {
    override fun code() = errorCode

    override fun msg() = errorMsg

    override fun data() = data

    override fun isSuccess() = errorCode == 0
}
```

**！！！！绝对要注意的是，Kotlin 的实体类，属性一定要都是可 null 的，千万不要预想服务端不可能返回 null，否则坑你没商量**

具体的 data 是什么类型再创建不同的实体类，如果是使用 Rx + Retrofit 请求网络，还得对状态码和异常进行统一的处理，这里不详细展开，我们要说的是如果你是使用协程去请求网络，**那么该如何统一处理状态码和异常呢**？

协程是直接返回实体的，如果发生了异常，比如：
1. 网络问题。
2. 服务端问题，404 等。
3. 其他未知的错误。

那么程序将会崩溃，因此必须 try-catch，那么问题来了，只能 try-catch 吗？那岂不是每一个调用都需要？这里可以使用一个第三方库：[NetworkResponseAdapter](https://github.com/haroldadmin/NetworkResponseAdapter) 可以很好的解决错误的问题，除了使用第三方库，还能自己封装吗？是可以的。

因为有了 IBaseResponse 接口，我们就可以统一处理了，在 BaseViewModel 中，提供了 **launch** 方法：

```kotlin
/**
 * 所有网络请求都在 viewModelScope 域中启动协程，当页面销毁时会自动取消
 */
fun <T> launch(
    block: suspend CoroutineScope.() -> IBaseResponse<T?>?,
    onSuccess: (() -> Unit)? = null,
    onResult: ((t: T) -> Unit),
    onFailed: ((code: Int, msg: String?) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
) = viewModelScope.launch {
    try {
        handleResult(withContext(Dispatchers.IO) { block() }, onSuccess, onResult, onFailed)
    } catch (e: Exception) {
        onFailed?.let { handleException(e, it) }
    } finally {
        onComplete?.invoke()
    }
}
```

还有处理结果和处理异常的方法：

```kotlin
/**
 * 处理请求结果
 *
 * [entity] 实体
 * [onSuccess] 状态码对了就回调
 * [onResult] 状态码对了，且实体不是 null 才回调
 * [onFailed] 有错误发生，可能是服务端错误，可能是数据错误，详见 code 错误码和 msg 错误信息
 */
private fun <T> handleResult(
    entity: IBaseResponse<T?>?,
    onSuccess: (() -> Unit)? = null,
    onResult: ((t: T) -> Unit),
    onFailed: ((code: Int, msg: String?) -> Unit)? = null
) {
    // 防止实体为 null
    if (entity == null) {
        onFailed?.invoke(entityNullable, msgEntityNullable)
        return
    }
    val code = entity.code()
    val msg = entity.msg()
    // 防止状态码为 null
    if (code == null) {
        onFailed?.invoke(entityCodeNullable, msgEntityCodeNullable)
        return
    }
    // 请求成功
    if (entity.isSuccess()) {
        // 回调成功
        onSuccess?.invoke()
        // 实体不为 null 才有价值
        entity.data()?.let { onResult.invoke(it) }
    } else {
        // 失败了
        onFailed?.invoke(code, msg)
    }
}

private fun handleException(
    e: Exception,
    onFailed: (code: Int, msg: String?) -> Unit
) {
    if (BuildConfig.DEBUG) {
        e.printStackTrace()
    }
    return if (e is HttpException) {
        onFailed(e.code(), e.message())
    } else {
        val log = Log.getStackTraceString(e)
        onFailed(
            notHttpException,
            "$msgNotHttpException, 具体错误是\n${if (log.isEmpty()) e.message else log}"
        )
    }
}
```

调用处如下：

```kotlin
showLoadingDialog()
launch({
        mModel.login("userName", "pwd")
    },
    onSuccess = {
        Log.i("NetworkViewModel", "commonLog - onResume: success")
    },
    onResult = {
        Log.i("NetworkViewModel", "commonLog - onResult: ${it.size}")
        resultCode.set(it.size.toString())
    },
    onFailed = { code, msg ->
        Log.i("NetworkViewModel", "commonLog - onFailed: $code, $msg")
    },
    onComplete = {
        dismissDialog()
    }
)
```

上述代码把请求网络过程中可能出现的问题都解决了，就连状态码是 null 也处理了，最大程度保证服务端异常的情况下应用不会受影响而崩溃。

## 使用 Rx 请求网络
```java
@GET("wxarticle/chapters/json")
Observable<BaseEntity<List<DemoEntity>>> demoGet();

HttpRequest.getService(WanAndroidApiService.class).demoGet().
    subscribeOn(Schedulers.io())
    // 界面销毁自动取消
    .doOnSubscribe(this::addSubscribe)
    .observeOn(AndroidSchedulers.mainThread())
    // 使用封装好的 Observer，
    .subscribe(new CommonObserver<List<DemoEntity>>() {
        @Override
        public void onStart() {
        }
        @Override
        public void onSuccess() {
        }

        @Override
        public void onFailed(int code, @Nullable String msg) {
        }

        @Override
        public void onResult(List<DemoEntity> result) {
            Log.i("MainViewModel", "commonLog - onResume: " + mBaseResult);
            MainViewModel.this.result.set(String.valueOf(mBaseResult.code()));
        }

        @Override
        public void onComplete() {
            Log.i("MainViewModel", "onComplete: ");
        }
    });
```

详见 sample-java 示例。


## 使用原生 Retrofit 的方法请求网络
如果你的请求接口返回的是 Call<xxxx>，Retrofit 提供了 enqueue 和 execute 两种异步和同步的方法来发起请求，通过回调来接收结果，如果你的项目很小，或者说只有一小部分地方需要请求服务器数据，那么完全可以不要 rx，通过如下方式：

```java
@GET("https://www.google.com")
Call<BaseEntity<List<DemoEntity>>> demoGet2();

// 原生 Retrofit 请求，只有 addSubscribe 了才可以在界面销毁时取消
addSubscribe(HttpRequest.request(HttpRequest.getService(WanAndroidApiService.class).demoGet2(),
    new CommonObserver<List<DemoEntity>>() {
        @Override
        public void onStart() {
        }

        @Override
        public void onSuccess() {
            super.onSuccess();
        }

        @Override
        public void onFailed(int code, @Nullable String msg) {
            super.onFailed(code, msg);
        }

        @Override
        public void onResult(List<DemoEntity> result) {
            Log.i("MainViewModel", "commonLog - onResume: " + mBaseResult);
            MainViewModel.this.result.set(String.valueOf(mBaseResult.code()));
        }

        @Override
        public void onComplete() {
            Log.i("MainViewModel", "onComplete: ");
        }
    }));
```

其中 BaseEntity 也是必须要实现 IBaseResponse 接口的。

详见 sample-java 示例。


# 全局配置类：GlobalConfig
很多开关都在这里，功能都会尽量做成可配置的，详见注释。


# 全局 Activity 管理器：AppActivityManager
通过 Application.registerActivityLifecycleCallbacks 监听所有的 Activity 生命周期，此类目的是可在任意位置操作所有的 activity


# 应用前后台监听 AppStateTracker
需要在配置文件中开启 lifecycleProcess 属性，即可使用该类了。


# VM 可以启动和结束界面
ViewModel 可以调用 finish 和 startActivity 方法来启动和结束界面，如果不需要这个功能，可全局设置 GlobalConfig.isViewModelNeedStartAndFinish 或局部复写 isViewModelNeedStartAndFinish 方法来禁用功能，避免多余实例的创建。


# 各种 BindingAdapter
以点击事件为例，加入了防止过快点击的功能，默认两次点击的间隔时间为 800 毫秒，详见 com.imyyq.mvvm.binding.viewadapter.view.ViewAdapter 类。

可通过配置 isInterval 属性为 false，禁用此功能，那么就和普通的 setOnClickListener 没区别了。如下：

```xml
<Button
    onClickCommand="@{viewModel.onNetwork}"
    isInterval="@{false}"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="网络请求"
    />
```

# ViewPager/ViewPager2
binding-collection-adapter 对于 vp 和 vp2，只支持 view，不支持 fragment 绑定。


# 加载中对话框 LoadingDialog
配置 GlobalConfig 中包含 loadingDialog 字样的属性，详见注释。

在 ViewModel 中调用 xxxLoadingDialogXXX 方法即可。


# 加载中第三方库 [LoadSir](https://github.com/KingJA/LoadSir)
如果你不喜欢弹出式的加载中对话框的方式，可以使用非对话框的加载中，这里使用到了 LoadSir 第三方库

需要在配置文件中开启 loadSir 属性。

1. 定义你自己的 callback，调用 GlobalConfig.initLoadSir 进行初始化。
2. 根据自己的业务需要，在 ViewModel 中调用 showLoadSirXXX 等方法
3. 复写 Activity/Fragment 中的 LoadSir 相关方法，详见注释

# 侧滑返回
1. 全局配置：GlobalConfig.isSupportSwipe，默认为 false，可全局设置为 true，那么所有继承自 BaseActivity 的都会拥有侧滑返回的功能。
2. 必须给开启了侧滑返回的 Activity 的 Theme 配置 android:windowIsTranslucent 为 true，框架会自动设置 android:windowBackground 为透明。必须为透明，否则侧滑显示的底色默认是白色
3. 如果有部分 Activity 不想开启侧滑，比如主页，那么可以复写 isSupportSwipe 返回 false 即可，或者全局设置为 false，只给部分 Activity 开启侧滑。
4. 受影响的生命周期：开启了 windowIsTranslucent 后，Activity 相当于透明了，那么如果有其他的 Activity 覆盖在上面，底下的 Activity 将不会回调 onStop。
比如 A 是开启侧滑的，打开了 B，B 全屏覆盖在了 A 上，那么 A 不会回调 onStop，其他的生命周期没影响。

# 数据库 Room
史上最好用的 ORM 数据库框架，没有之一，配合 LiveData，Rx，协程等，用起来真的非常爽。

需要在配置文件中开启 room 属性，框架只提供了一个工具类 RoomUtil，用来获取数据库实例的，其他的 DAO，Entity 等自行根据业务进行设计。


# 工具类
框架默认添加了部分工具类，都在 com.imyyq.mvvm.utils 包下，有需要的自行查看。
