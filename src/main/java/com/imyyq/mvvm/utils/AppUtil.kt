package com.imyyq.mvvm.utils

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.imyyq.mvvm.app.BaseApp

object AppUtil {
    private const val TAG = "AppUtil"

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
}
