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

因为几乎每个页面都会有个 vm，所以可以把变量放到 Live Template 中，这样就可以快速生成了。


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
    override fun isNeedLoadingDialog(): Boolean {
        return super.isNeedLoadingDialog()
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

像 isNeedLoadingDialog 这类的方法，是功能的开关，默认是开启的，但是当你的界面不需要对话框时，可以复写并返回 false，那么框架将不会实例化相关变量，减少内存占用，其他类似方法也是一样的。

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
    override fun onResume(owner: LifecycleOwner) {
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
