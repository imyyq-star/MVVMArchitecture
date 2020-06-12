# 简介
采用 Kotlin 编写的 MVVM 框架，采用的库如下：

* androidx
    * ConstraintLayout
    * RecyclerView
    * AppActivityManager
* LiveData 生命周期感知
* Lifecycles 生命周期感知
* ViewModel 业务逻辑层
* Room 数据库
* Glide 图片加载
* Retrofit2 网络
* Gson JSON 解析，或者使用 ProtocolBuffer
* DataBinding、bindingAdapter 数据绑定
* Kotlin 协程替代 Rx
* LivePermissions 权限申请


# View 视图层
视图层，优先采用 ConstraintLayout，但是明显是线性布局的，采用 LinearLayout，简单列表采用 ListView，简单网格采用 GridView，复杂的用 RecyclerView。
下拉刷新上拉加载优先用 scwang90/SmartRefreshLayout，如果只有下拉刷新，采用 SwipeRefreshLayout。
封装 BaseActivity/BaseFragment，持有 ViewModel 和 ViewDataBinding 的实例，这样用户的操作可以流向 VM 层。

在 V 层发起异步，可使用 lifecycleScope 或 lifecycle.coroutineScope 协程，让异步跟随生命周期，在生命周期结束时取消异步。

所以 Activity/Fragment + XML 组成 V 层。


# ViewModel 业务逻辑层
继承自 ViewModel 或 AndroidViewModel，持有 Repository 仓库实例，可以向仓库请求数据，通过 LiveData 或 DataBinding 将数据流回 V 层。
通过 Lifecycles 感知 V 层的生命周期，VM 层的生命周期和 V 层保持一致，只有 V 完全销毁时，onCleared 方法才会回调。

在 VM 层发起异步或调用 Repository 数据，使用 viewModelScope 生命周期感知型协程替代 Rx，只有在界面销毁时可自动取消任务。
或者使用 liveData 协程，这样在 liveData 被激活时自动执行协程，如果未执行完成就被取消，那么下次激活时还会自动执行。


# Model 数据层
主要用 Repository 仓库来表示，Retrofit2 用于请求网络，Gson 用于解析 JSON，Room 用于存储本地数据，访问网络、数据库、文件等，均可以使用协程来实现异步。

