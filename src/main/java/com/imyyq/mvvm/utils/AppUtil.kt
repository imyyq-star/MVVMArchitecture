package com.imyyq.mvvm.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.provider.Settings
import com.imyyq.mvvm.app.BaseApp

object AppUtil {
    private val TAG = "AppUtil"

    /**
     * 获取当前进程的名称，默认进程名称是包名
     */
    val currentProcessName: String?
        get() {
            val pid = android.os.Process.myPid()
            val mActivityManager = BaseApp.getInstance().getSystemService(
                Context.ACTIVITY_SERVICE
            ) as ActivityManager
            for (appProcess in mActivityManager.runningAppProcesses) {
                if (appProcess.pid == pid) {
                    return appProcess.processName
                }
            }
            return null
        }

    /**
     * @return 获取所有的带启动图标的App
     */
    val launcherResolveInfoList: List<ResolveInfo>
        get() {
            val intent = Intent()
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            return BaseApp.getInstance().packageManager.queryIntentActivities(intent, 0)
        }

    /**
     * 取得版本名称
     */
    val versionName: String
        get() = defPackageInfo!!.versionName

    /**
     * 取得版本号
     */
    val versionCode: Int
        get() = defPackageInfo!!.versionCode

    /**
     * 获取应用的信息，0代表是获取版本信息
      */
    val defPackageInfo: PackageInfo?
        get() {
            val packageManager = BaseApp.getInstance().packageManager
            var packInfo: PackageInfo? = null
            try {
                packInfo = packageManager.getPackageInfo(BaseApp.getInstance().packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return packInfo
        }

    /**
     * App 的名称
     */
    val appLabel: String?
        get() {
            val packageManager = BaseApp.getInstance().packageManager
            try {
                return packageManager.getApplicationLabel(
                    packageManager.getApplicationInfo(
                        BaseApp.getInstance().packageName,
                        0
                    )
                ).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                LogUtil.e(TAG, "getDefPackageInfo: " + e.message)
            }

            return null
        }

    /**
     * 系统分享文本的 Intent
     */
    fun shareTextIntent(text: String): Intent {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        sendIntent.type = "text/plain"
        return sendIntent
    }


    /**
     * 指定包名[packageName]的 App 是否已安装到设备上
     */
    fun isAppExist(packageName: String): Boolean {
        val packageManager = BaseApp.getInstance().packageManager
        // 获取所有已安装程序的包信息
        val list = packageManager.getInstalledPackages(0)
        for (i in list.indices) {
            if (list[i].packageName.equals(packageName, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    /**
     * 打开应用的详细信息设置
     */
    fun gotoAppDetailsSettings(context: Context, requestCode: Int, packageName: String = context.packageName) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        if (context is Activity) {
            context.startActivityForResult(intent, requestCode)
        }
    }

    /**
     * 打开指定包名的应用
     */
    fun startActivityByPackage(context: Context, packageName: String): Boolean {
        val pm = context.packageManager
        val intent = pm.getLaunchIntentForPackage(packageName)
        if (null != intent) {
            context.startActivity(intent)
            return true
        }
        return false
    }

    /**
     * 打开其他的Activity
     *
     * @param cls 要打开的Activity
     */
    fun startActivity(context: Context?, cls: Class<*>) {
        if (context == null) {
            return
        }
        context.startActivity(Intent(context, cls))
    }

    fun startActivity(context: Context, cls: Class<*>, name: String, value: Int) {
        val intent = Intent(context, cls)
        intent.putExtra(name, value)
        context.startActivity(intent)
    }

    fun startActivity(context: Context, cls: Class<*>, name: String, value: Boolean) {
        val intent = Intent(context, cls)
        intent.putExtra(name, value)
        context.startActivity(intent)
    }

    /**
     * 打开其他的Activity，并附带字符串
     *
     * @param cls   要打开的Activity
     * @param name  字符串名称
     * @param value 字符串值
     */
    fun startActivity(context: Context, cls: Class<*>, name: String, value: String) {
        val intent = Intent(context, cls)
        intent.putExtra(name, value)
        context.startActivity(intent)
    }

    fun startActivityForResult(
        context: Activity, cls: Class<*>, requestCode: Int,
        name: String, value: String
    ) {
        val intent = Intent(context, cls)
        intent.putExtra(name, value)
        context.startActivityForResult(intent, requestCode)
    }
}
