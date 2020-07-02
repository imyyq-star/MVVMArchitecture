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

    recyclerView=false

    room=false
    swipeRefreshLayout=false
    retrofit2=false
    glide=false

    // 权限申请
    livePermissions=false

    ... 其他的详见文件
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

* 6、其他的库
在配置文件中的库，都是有经过框架二次封装的，没有配置的库，可在 dependencies.gradle 中查看，根据需要自行引入，比如各种 ktx：
```groovy
implementation Deps.natigationFragmentKTX
```
