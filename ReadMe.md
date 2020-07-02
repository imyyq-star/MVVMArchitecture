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

# 基本配置
详见 [BasicConfiguration.md](document/BasicConfiguration.md)

# 基本使用
详见 [BaseUse.md](document/BaseUse.md)

# 其他使用建议
详见 [MoreUse.md](./document/MoreUse.md)


# 混淆
框架已配置了相关的混淆规则，使用者只需要配置自己的规则即可。

# 可能遇到的问题
详见 [FAQ.md](document/FAQ.md)

# 文件模板
详见 [Template.md](document/Template.md)


# 关于定制化的声明
本人测试能力有限，不可能对所有的库的冲突性进行测试，所以如果你在开发中，有遇到什么配置冲突的，请提 Issue
