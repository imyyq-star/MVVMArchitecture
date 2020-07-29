/**
 * 功能：通过代码达到以下效果
 * 1、批量创建整个目录结构
 * 2、创建 source 等空文件
 * 3、创建 Application 等非空文件
 *
 * 使用方法：
 * 1. 打开 Tools | Groovy Console，Project 窗口将项目的目录层级结构切换为 “Project”，即可看到 Scratches and Consoles/Groovy consoles 下的 groovy 文件，每打开一次就有一个 groovy 文件生成。
 * 2. 复制以下全部代码到该 groovy 文件内。
 * 3. 修改包含 TODO 标注的几个变量为你的真实目录地址和真实包名
 * 4. 点击当前 groovy 文件左上角的绿色三角按钮即可生成推荐的目录结构
 * 5. 生成后 AS 不会自动同步，需要右键单击项目，选择 Reload from Disk
 */

new Main().main()

class Main {
    /**
     * TODO 目标目录的绝对路径，即生成的文件要放在哪里，如下目录的话结果就是在 com.test.mvvmarchitecturetest 目录下生成 app、data 等目录和文件：
     * MVVMArchitectureTest
     *      -app
     *          -src
     *              -main
     *                  -java
     *                      -com.test.mvvmarchitecturetest
     *                          -app
     *                          -data
     *                          -ui
     *                          -.....
     */
    String mAbsolutePath = "E:\\Projects\\MVVMArchitectureTest\\app\\src\\main\\java\\com\\test\\mvvmarchitecturetest"

    // TODO 应用的包名，即 build.gradle 中的 applicationId
    String mPackageName = "com.test.mvvmarchitecturetest"
    // TODO 通常和应用包名一致，即上面的路径 com.test.mvvmarchitecturetest，如果你的目录结构和包名不一致就需要改
    String mRootPackage = "${mPackageName}"
    // TODO 框架的包名，框架默认是 com.imyyq.mvvm，如果你修改了，也要同步改
    String mArchRootPackage = "com.imyyq.mvvm"

    // TODO 生成 Java 代码还是 Kotlin？
    boolean mIsJava = false

    String dirApp = "app"
    String dirEntity = "entity"
    String dirUi = "ui"
    String dirUtils = "utils"

    String dirDataSource = "data/source"
    String dirDataSourceHttp = "$dirDataSource/http"
    String dirDataSourceLocal = "$dirDataSource/local"
    String dirDataSourceHttpService = "$dirDataSourceHttp/service"

    String packageDataSource = dirDataSource.replace("/", ".")
    String packageDataSourceHttp = dirDataSourceHttp.replace("/", ".")
    String packageDataSourceLocal = dirDataSourceLocal.replace("/", ".")
    String packageDataSourceHttpService = dirDataSourceHttpService.replace("/", ".")

    String application = "AppApplication"
    String httpDataSource = "HttpDataSource"
    String localDataSource = "LocalDataSource"
    String httpDataSourceImpl = "HttpDataSourceImpl"
    String localDataSourceImpl = "LocalDataSourceImpl"
    String apiService = "ApiService"
    String baseEntity = "BaseEntity"

    void main() {

        createDir(dirApp)
        createDir(dirDataSourceHttpService)
        createDir(dirDataSourceLocal)
        createDir(dirEntity)
        createDir(dirUi)
        createDir(dirUtils)

        if (mIsJava) {
            createFile(dirApp, "${application}.java", getJavaApplication(application))
            createFile(dirDataSource, "${httpDataSource}.java", getJavaInterface(packageDataSource, httpDataSource))
            createFile(dirDataSource, "${localDataSource}.java", getJavaInterface(packageDataSource, localDataSource))
            createFile(dirDataSourceHttpService, "${apiService}.java", getJavaInterface(packageDataSourceHttpService, apiService))

            createFile(dirDataSourceHttp, "${httpDataSourceImpl}.java", getJavaHttpDataImpl())
            createFile(dirDataSourceLocal, "${localDataSourceImpl}.java", getJavaLocalDataImpl())
            createFile(dirEntity, "${baseEntity}.java", getJavaBaseEntity())
        } else {
//            createFile(dirApp, "${application}.kt", getKotlinApplication(application))
//            createFile(dirDataSource, "${httpDataSource}.kt", getKotlinInterface(packageDataSource, httpDataSource))
//            createFile(dirDataSource, "${localDataSource}.kt", getKotlinInterface(packageDataSource, localDataSource))
//            createFile(dirDataSourceHttpService, "${apiService}.kt", getKotlinInterface(packageDataSourceHttpService, apiService))
//
//            createFile(dirDataSourceHttp, "${httpDataSourceImpl}.kt", getKotlinHttpDataImpl())
//            createFile(dirDataSourceLocal, "${localDataSourceImpl}.kt", getKotlinLocalDataImpl())
//            createFile(dirEntity, "${baseEntity}.kt", getKotlinBaseEntity())

            String activity = "MainActivity"
            String vm = "MainViewModel"
            String xml = "activity_main"

            createFile(dirUi, "${activity}.kt", getKotlinMainActivity(activity, vm, xml))
            createFile(dirUi, "${vm}.kt", getKotlinMainViewModel(vm))
        }
    }

    String getKotlinInterface(String relativePackage, String interfaceName) {
        return """package $mRootPackage.$relativePackage

interface $interfaceName {
}
"""
    }

    String getKotlinApplication(String className) {
        return """package ${mRootPackage}.${dirApp}

import ${mArchRootPackage}.app.BaseApp
import ${mArchRootPackage}.app.GlobalConfig
import ${mArchRootPackage}.http.HttpRequest

class ${className} : BaseApp() {
    override fun onCreate() {
        super.onCreate()
        // 如果有用到网络请求，使用 HttpRequest 获取 service 实例，需要在这里设置 baseUrl
        HttpRequest.mDefaultBaseUrl = ""
        // 如果有通用的请求头，可以统一设置
        HttpRequest.addDefaultHeader("", "")

        // 可在这里全局配置功能，详见 GlobalConfig
        GlobalConfig.isSupportSwipe = false
    }
}
"""
    }

    String getKotlinLocalDataImpl() {
        return """package ${mRootPackage}.${packageDataSourceLocal}

import ${mRootPackage}.${packageDataSource}.${localDataSource}

object ${localDataSourceImpl} : ${localDataSource} {
}
"""
    }

    String getKotlinHttpDataImpl() {
        return """package ${mRootPackage}.${packageDataSourceHttp}

import ${mRootPackage}.${packageDataSource}.${httpDataSource}
import ${mRootPackage}.${packageDataSourceHttpService}.${apiService}


object ${httpDataSourceImpl} : ${httpDataSource} {
    lateinit var mApiService: ${apiService}
}
"""
    }

    String getKotlinBaseEntity() {
        return """package ${mRootPackage}.${dirEntity}

import ${mArchRootPackage}.base.IBaseResponse

data class BaseEntity<T>(
    var data: T?,
    var code: Int?,
    var msg: String?
) : IBaseResponse<T> {
    override fun code() = code

    override fun msg() = msg

    override fun data() = data

    override fun isSuccess() = code == 0
}
"""
    }

    String getKotlinMainActivity(String className, String vmName, String xmlName) {
        String []strings = xmlName.split("_")
        StringBuilder builder = new StringBuilder()
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i]
            builder.append(s.charAt(0).toUpperCase())
            builder.append(s.substring(1))
        }
        builder.append("Binding")
        return """package ${mRootPackage}.${dirUi}

import ${mPackageName}.BR
import ${mPackageName}.R
import ${mRootPackage}.databinding.${builder.toString()}
import ${mArchRootPackage}.base.BaseActivity

class $className : BaseActivity<${builder.toString()}, $vmName>(
    R.layout.$xmlName,
    BR.viewModel
) {
}
"""
    }

    String getKotlinMainViewModel(String className) {
        return """package ${mRootPackage}.${dirUi}

import android.app.Application
import ${mArchRootPackage}.base.BaseModel
import ${mArchRootPackage}.base.BaseViewModel

class $className(app: Application) : BaseViewModel<BaseModel>(app) {
}
"""
    }

    void createDir(String dir) {
        File parent = new File(mAbsolutePath)
        new File(parent, dir).mkdirs()
    }

    void createFile(String relative, String fileName, String content) {
        File parent = new File(mAbsolutePath + "\\" + relative)
        parent.mkdirs()
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(parent, fileName))))
        writer.write(content)
        writer.close()
    }

    String getJavaInterface(String relativePackage, String interfaceName) {
        return """package $mRootPackage.$relativePackage;

public interface $interfaceName {
}
"""
    }

    String getJavaApplication(String className) {
        return """package ${mRootPackage}.${dirApp};

import ${mArchRootPackage}.app.BaseApp;

public class $className extends BaseApp {
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
"""
    }

    String getJavaLocalDataImpl() {
        return """package ${mRootPackage}.${packageDataSourceLocal};

import ${mRootPackage}.${packageDataSource}.${localDataSource};

public class ${localDataSourceImpl} implements ${localDataSource} {
    private volatile static ${localDataSourceImpl} INSTANCE = null;

    public static ${localDataSourceImpl} getInstance() {
        if (INSTANCE == null) {
            synchronized (${localDataSourceImpl}.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ${localDataSourceImpl}();
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private ${localDataSourceImpl}() {
    }
}
"""
    }

    String getJavaHttpDataImpl() {
        return """package ${mRootPackage}.${packageDataSourceHttp};

import ${mRootPackage}.${packageDataSource}.${httpDataSource};
import ${mRootPackage}.${packageDataSourceHttpService}.${apiService};

public class ${httpDataSourceImpl} implements ${httpDataSource} {
    private ${apiService} apiService;
    private volatile static ${httpDataSourceImpl} INSTANCE = null;

    public static ${httpDataSourceImpl} getInstance(${apiService} apiService) {
        if (INSTANCE == null) {
            synchronized (${httpDataSourceImpl}.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ${httpDataSourceImpl}(apiService);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private ${httpDataSourceImpl}(${apiService} apiService) {
        this.apiService = apiService;
    }
}
"""
    }

    String getJavaBaseEntity() {
        return """package ${mRootPackage}.${dirEntity};

import androidx.annotation.Nullable;

import ${mArchRootPackage}.base.IBaseResponse;

public class BaseEntity<T> implements IBaseResponse<T> {
    public T data;
    public int code;
    public String msg;

    @Nullable
    @Override
    public Integer code() {
        return code;
    }

    @Nullable
    @Override
    public String msg() {
        return msg;
    }

    @Nullable
    @Override
    public T data() {
        return data;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
}
"""
    }
}