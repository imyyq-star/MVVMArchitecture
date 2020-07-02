# 可能遇到的问题

## 1. BR 的问题
有些时候 BR 类不会自动导入，需要你手动 import，甚至是导入后还是报红色，但其实已经可以运行了，这是 AS 的锅。

## 2. Room 数据库
```
Caused by: java.lang.RuntimeException: cannot find implementation for com.xxx.xxx.xx.Xxxx Xxxx_Impl does not exist
```

就是说没有生成 Impl 类。解决方案如下：

Java 需要开启：
```groovy
dependencies {
    annotationProcessor Deps.roomCompiler
}
```

Kotlin 需要开启 kapt，并在 build.gradle 中加入：
```groovy
dependencies {
    kapt Deps.roomCompiler
}
```

## 3. 引用冲突
```
Unable to resolve dependency for ':MVVMArchitecture@debug/compileClasspath': Could not resolve xx.xxx.xxx:xxx:x.x.x.
```

发生上述冲突的原因是因为框架有库以 api 和 compileOnly 两种方式引用了。
举个例子做个假设，roomRxJava 会引入 RxJava，而 retrofit2RxJava2 也会引入 RxJava，如果框架中的 build.gradle 文件按照以下来引用：

```groovy
dependencies {
    if (false) {
        api Deps.roomRuntime
        api Deps.roomRxJava
        kapt Deps.roomCompiler
    } else {
        compileOnly Deps.roomRuntime
        compileOnly Deps.roomRxJava
    }

    if (true) {
        api Deps.retrofit2RxJava2
    }
}
```

compileOnly 是为了框架中有用到 room 的代码不要编译报错。以上是没有开启 room，只开启了 retrofit2RxJava2，那么就会报错，因为 RxJava 在上面的 else 中已经是 compileOnly 的了，下面的 if 却是 api 的。

解决方法是：
1. 因为框架没有代码用到 roomRxJava，所以可以不用 compileOnly。
2. 如果用到了，就得想办法根据实际情况进行 exclude 了。

## 4. MVVMArchitecture/mvvm-config.groovy.template 和项目目录下的 mvvm-config.groovy 的冲突
```
Caused by: java.io.NotSerializableException: groovy.util.ConfigObject
```

两个文件的属性没有同步，比如根目录下的文件少了某个属性。
