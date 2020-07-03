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

或者进入到 MVVMArchitecture 目录，执行正常的仓库更新：

```shell script
git pull origin master
```

克隆完成后，你的项目仓库会出现两个文件：.gitmodules，MVVMArchitecture，这两个文件是要被纳入仓库中管理起来的，每次更新了框架的代码，MVVMArchitecture 文件将会变化，该变化需要被仓库管理起来。

具体这两个文件是什么作用，请自行查阅 git 子模块是如何使用的。

现在，整个框架的源码都拷贝下来了，下面我们进行一些基本的配置，就可以使用框架了。

# 框架特点
* 吸取了 MVVMHabit/MVVMLin 的优点，使用 Kotlin 重写，兼容 Java，建议使用者优先使用 Kotlin。
* 优先使用 Kotlin 的协程和 Flow，保留了对 Rx 的支持。
* 通过文件模板，代码模板，create_templates.groovy 等手段，快速创建项目模板，快速创建 activity 和 viewModel。
* 使用 DataBinding 可大幅度减少样板代码，虽然 DataBinding 的口碑不是很好，但使用熟练了真的非常好用。
* 可定制化，需要用到的库才会被打进包里，需要的功能才会被实例化，尽可能的全局和局部可配置。无论大小应用都可以使用。

# 基本配置
详见 [BasicConfiguration.md](document/BasicConfiguration.md)

# 基本使用
详见 [BaseUse.md](document/BaseUse.md)

# 其他使用建议
详见 [MoreUse.md](./document/MoreUse.md)


# 混淆
框架已配置了相关的混淆规则，使用者只需要配置自己的规则即可。

最大限度打包不加入不要的类。

## 场景 1
* 框架配置了混淆规则
* 项目没有开启混淆，框架开启了混淆
* 代码中有封装到该库
* 没有启用该库
* 项目中没有使用到框架的东西，只是引用了框架

结果：会把封装到的相关类打进去，但是非常的小，比如 LoadSir，rx 等

## 场景 2
相比于场景 1，只是把项目的混淆也开启了，那么没有用到的类，都会被清除出去

## 场景 3
相比于场景 1，把项目的混淆也开启了，用到的 BaseActivity 和 BaseApp 等类，其他没有用到的依然没有被打进去

## 场景 3
相比于场景 1，把项目的混淆也开启了，用到的 BaseActivity 和 BaseApp 等类，开启了 glide 和 LoadSir，其他没有用到的没有被打进去，但是这两个开启的用到的都会被打进去。似乎没用到的会自动去掉？

## 场景 4
相比于场景 1，把项目的混淆也开启了，用到的 BaseActivity 和 BaseApp 等类，手动引入没有封装也没有用到的库，比如 viewPager2，还是会加入进去的

## 结论
定制化完全可行，无论你的项目是大是小，都适合于本框架。

# 可能遇到的问题
详见 [FAQ.md](document/FAQ.md)

# 文件模板
详见 [Template.md](document/Template.md)


# 关于定制化的声明
本人测试能力有限，不可能对所有的库的冲突性进行测试，所以如果你在开发中，有遇到什么配置冲突的，请提 Issue
