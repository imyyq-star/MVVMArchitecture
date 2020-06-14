# 获取源代码
框架没有通过远程仓库的形式发布，考虑到需求是多种多样的，而且此类型的框架并没有说一定就是固定的，因此我期望它是随时可改的，同时也能具备随时更新的能力，因此，我决定采用 Git 子模块的方式，即 submodule。

使用命令行进入要引入框架的目录，即项目根目录，执行以下命令：

```shell script
git submodule add https://xxxxx
```
后缀是此仓库的地址，**如果你想对本框架提交代码做出贡献，请先 fork 到你的仓库中再执行此命令**。

即可将框架作为子模块引入，后续如果本框架有更新，可执行以下命令进行更新：

```shell script
git submodule update --remote
```

克隆完成后，你的项目仓库会出现两个文件：.gitmodules，MVVMArchitecture，这两个文件是要被纳入仓库中管理起来的，每次更新了框架的代码，MVVMArchitecture 文件将会变化，该变化需要被仓库管理起来。

具体这两个文件是什么作用，请自行查阅 git 子模块是如何使用的。

现在，整个框架的源码都拷贝下来了，下面我们进行一些基本的配置，就可以使用框架了。

# 基本配置
* 1、在 settings.gradle 中加入框架：

```groovy
include ':MVVMArchitecture'
```

* 2、项目根目录下的 build.gradle 引用 dependencies.gradle 文件（如下）

```groovy
buildscript {
    apply from: 'MVVMArchitecture/dependencies.gradle'

    addRepository(repositories)

    dependencies {
        // 根据 AS 的版本而不同
        classpath "com.android.tools.build:gradle:x.x.x"
        classpath Deps.kotlinPlugin
    }
}

allprojects {
    addRepository(repositories)
}

configurations.all {
    resolutionStrategy {
        // 有需要的话，这里可以强制使用某个版本的库
        force 'androidx.annotation:annotation:1.1.0',
            'androidx.arch.core:core-common:2.0.1',
            'androidx.arch.core:core-runtime:2.0.1',
            'androidx.core:core:1.0.1'
    }
}

...

```

* 3、模块（比如 app）目录下的 build.gradle 必须启用如下功能，并引用 MVVMArchitecture 模块

```groovy
// Kotlin 需要增加 kapt plugin 才可以使用 DataBinding
apply plugin: 'kotlin-kapt'

android {
    // 指定 sdk 相关信息，下面会说如何配置
    compileSdkVersion SysConfig.compileSdkVersion

    defaultConfig {
        applicationId "com.xxxx.xxxxx"

        minSdkVersion SysConfig.minSdkVersion
        targetSdkVersion SysConfig.targetSdkVersion

        ...
    }

    // AS 4.0 及以上
    buildFeatures{
        dataBinding = true
    }

    // AS 4.0 以下
    dataBinding {
        enabled = true
    }
    
    // 以上 DataBinding 配置二选一

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "${JavaVersion.VERSION_1_8}"
    }

}
dependencies {
    ...

    // 将 appcompat，core-ktx，constraintlayout，kotlin 等默认添加的去掉，只留下 test 和 MVVMArchitecture 即可
    implementation project(path: ':MVVMArchitecture')

    ...
}
```

* 4、生成配置文件，指定 SDK 版本，开启相关需要的库。
拷贝 MVVMArchitecture/MVVMArchitecture-config.groovy.template 到项目根目录，重命名为 mvvm-config.groovy。
配置 SDKVersion，include 指定你需要包含哪些库。

```groovy
/**
 * 决定需要开启哪些库的使用，有些库是有关联的，比如 bindingCollection 引用了 recyclerView。
 * 因此即使禁用掉 recyclerView，开启 bindingCollection，也会导入 recyclerView
 *
 * 禁用掉的库，就不能使用该库的内容，正常来说在 release 打包的时候会从包中移除
 */
include {
    // 内存泄露，默认只在 debug 期间生效
    leakCanary2=true
    // 应用前后台监听
    lifecycleProcess=true

    // 通常来说以上都需要为 true

    bindingCollection {
        // ViewPager 和 ListView 等
        bindingAdapter=false
        bindingAdapterRv=false
        bindingAdapterVp2=false
    }
    recyclerView=false
    material=false
    viewPager2=false

    room=false
    swipeRefreshLayout=false
    retrofit2=false
    gson=false
    glide=false

    // 权限申请
    livePermissions=false

    paging=false
    rxJava2=false
    rxKotlin=false

    // 各种 KTX，详见 https://developer.android.google.cn/kotlin/ktx
    reactiveStreamsKTX=false

    natigationUiKTX=false
    natigationRuntimeKTX=false
    natigationFragmentKTX=false

    roomKTX=false
    sqliteKTX=false
    workKTX=false
    paletteKTX=false
    collectionKTX=false
}

SDKVersion {
    compileSdkVersion=29
    minSdkVersion=21
    targetSdkVersion=29
}
```

都是 Google 官方的库居多，具体是什么库，大部分见名知意，如果还不懂是什么库，可查看 dependencies.gradle 文件。

* 5、sync
此时基本配置已经完成了，默认拥有的能力除了配置文件中值为 true 的属性，还有如下：

```groovy
// 必备的 4 个库
api Deps.kotlinStdLib
api Deps.kotlinKTX
api Deps.appcompat
api Deps.constraintLayout

/**
 * 在 Lifecycles 中使用协程，视图销毁则自动取消
 */
api Deps.lifecycleRuntimeKTX
/**
 * 在 LiveData 中使用协程
 */
api Deps.lifecycleLiveDataKTX
/**
 * 简化 Fragment 的 API 调用，同时通过 viewModels 扩展方法创建 vm 来的非常容易
 */
api Deps.fragmentKTX
/**
 * 在 vm 中使用协程，视图销毁自动取消
 */
api Deps.viewModelKTX
/**
 * 生成生命周期注解相关的代码，否则将反射调用，影响性能
 */
kapt Deps.lifecycleCompiler
```

其他的库，**根据自己的需要自行开启和关闭，这是框架的一个亮点，可以有效减少使用不到的库**。比如你不需要使用到数据库，那么可以将 room 属性配置为 false，打包时将不会引入 room 的相关内容，你也不能使用相关的 API，否则运行时将找不到类。

# 基本使用

## 配置 Application
要么继承 BaseApp，要么在调用 BaseApp 的 init 方法：
```kotlin
BaseApp.initApp(this)
```

## 创建 xml
```xml
<?xml version="1.0" encoding="utf-8"?>

<!--
必须用 layout 包裹，DataBinding 的相关知识请自行查阅。
-->
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    >

    <data>

        <!--
        声明变量，通常有 vm 的界面，都需要声明对应的 vm 变量
        在构造界面时把该变量传给 BaseXXXX
        -->
        <variable
            name="viewModel"
            type="com.imyyq.sample.MainViewModel"
            />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        tools:context=".MainActivity"
        >

        ...

    </LinearLayout>
</layout>
```

这里有个技巧，在 xml 根目录执行 Alt + Enter，可快速用 layout 标签包裹布局。

![图1](https://imyyq.coding.net/p/MyMarkdownImg/d/MyMarkdownImg/git/raw/master/20200614/86482a4137bf467381c5eeb8a3a60b4903a1ec42318dbbc3880635ab2576fcf8.png)  


## 创建 View 层
```kotlin
/**
 * View 层需要继承相应的基类：BaseFragment/BaseActivity
 *
 * ActivityMainBinding 是 R.layout.activity_main 生成的 binding 类，是 DataBinding 相关的知识。
 * MainViewModel 是界面的主 vm，如果你的界面没有 vm，可以用 BaseViewModel。
 *
 * 构造需传入两个参数，一个是界面的 xml 界面，另一个是 vm 在 xml 中的变量，详见 xml 的配置。
 * 如果 xml 没有配置 vm 变量，可不传。
 */
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(
    R.layout.activity_main, BR.viewModel
) {
    // 是否保持界面常亮。
    override fun isKeepScreenOn(): Boolean {
        return super.isKeepScreenOn()
    }

    // 是否需要对话框
    override fun isNeedDialog(): Boolean {
        return super.isNeedDialog()
    }

    // vm 是否需要启动和结束界面
    override fun isViewModelNeedStartAndFinish(): Boolean {
        return super.isViewModelNeedStartAndFinish()
    }

    /**
     * 打开这个界面时传入的参数可以在这里处理。
     *
     * 此时 mViewModel 和 mBinding 还没有实例化。
     */
    override fun initParam() {
    }

    /**
     * 这个方法是用来绑定 vm 中的响应式变量到界面中的，比如 LiveData。
     *
     * 此时 mViewModel 和 mBinding 已实例化。
     */
    override fun initViewObservable() {
        // mViewModel 是界面关联的主 VM 的实例，有上述的泛型参数决定，这里是 MainViewModel。
        // mBinding 是 layout 文件的绑定类，包含了声明了 id 的所有 view 的引用。
        Log.i("MainActivity", "initViewObservable: $mViewModel, $mBinding")
    }

    /**
     * 这个方法被调用时，此时界面已经初始化完毕了，可以进行获取数据的操作了，比如请求网络。
     */
    override fun initData() {
    }

    /**
     * 如果你的 vm 是通过 自定义 factory 创建的，可复写此方法。
     * 否则将由框架帮你实例化 vm。
     */
    override fun initViewModel(viewModelStoreOwner: ViewModelStoreOwner): MainViewModel {
        return super.initViewModel(viewModelStoreOwner)
    }
}
```

要注意的是，上述方法都不是必须复写的，一切配置都尽量可选。

View 层会持有 ViewModel 层的实例 mViewModel，xml 中也持有了 VM 层的实例，**意味着 View 层的所有操作都可以流向 ViewModel 层**。

也会持有 View 层的实例 mBinding。mBinding 就完全可以消灭掉 findViewById 了，虽然 kotlin-android-extensions 也有这样的功能，但是 mBinding 可读性更好。

像 isNeedDialog 这类的方法，是功能的开关，默认是开启的，但是当你的界面不需要对话框时，可以复写并返回 false，那么框架将不会实例化相关变量，减少内存占用，其他类似方法也是一样的。

其他说明都在注释里了。

## 创建 ViewModel 层
```kotlin
/**
 * ViewModel 层需要继承 BaseViewModel。最终继承自 AndroidViewModel，这样可拥有 Application 的实例。
 *
 * 如果没有数据仓库，可以使用 BaseModel 作为 Model 层。
 */
class MainViewModel(app: Application) : BaseViewModel<BaseModel>(app) {
    /**
     * vm 可以感知 v 的生命周期
     */
    override fun onResume() {
        // 注意！！！！！ vm 层绝对不可以引用 v 层的实例，需要 context 要么通过 application，要么通过 AppActivityManager
        val app = getApplication<MyApp>()
        Log.i("MainViewModel", "commonLog - onResume: $app")
    }
}
```

**一定要注意的是：vm 层绝对不可以引用 v 层的实例**，否则会造成内存泄露。

vm 的数据流向 v 层，只能通过 DataBinding 或 LiveData。

## 创建 Model 层
按照 Google 的设计建议，Model 数据层应该是一个仓库 Repository，对外暴露数据接口，使用者无需知道数据是从哪里来的，是本地还是网络还是其他什么的对使用者来说都不重要。

这块请查看 [MVVMArchitectureSample](https://github.com/imyyq-star/MVVMArchitectureSample)


## 列表绑定
这部分采用第三方库 [binding-collection-adapter](https://github.com/evant/binding-collection-adapter)，在配置文件中开启 bindingCollection 相关的配置即可，具体用法详见此库的用法。


## 图片加载
图片加载使用 Glide，使用 DataBinding 可以很方便的绑定图片地址和占位图：

```xml
<ImageView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    url="@{viewModel.mImageUrl}"
    placeholderRes="@{R.mipmap.ic_launcher}"
/>
```

具体如何实现的，详见 com.imyyq.mvvm.binding.viewadapter.image 包下的代码。其实 DataBinding 对于 View 属性相关的绑定，自定义的绑定是不需要加前缀的，如上面的 url 绑定，如果 View 有定义 setXXXX 方法，那么使用 DataBinding 就会有 app:xXXX 属性：

```xml
<ImageView
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:visibility="@{View.GONE}"
/>
```


## 网络请求
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

具体详见 [MVVMArchitectureSample](https://github.com/imyyq-star/MVVMArchitectureSample)


# 工具类
框架默认添加了部分工具类，都在 com.imyyq.mvvm.utils 包下，有需要的自行查看。

# 混淆
框架已配置了相关的混淆规则，使用者只需要配置自己的规则即可。
