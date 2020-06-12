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

    val currentProcessName: String?
        get() {
            val pid = android.os.Process.myPid()
            val mActivityManager = BaseApp.getInstance().getSystemService(
                    Context.ACTIVITY_SERVICE) as ActivityManager
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
    val launchableResolveInfos: List<ResolveInfo>
        get() {
            val intent = Intent()
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            return BaseApp.getInstance().packageManager.queryIntentActivities(intent, 0)
        }

    /**
     * 取得版本名称，对应 AndroidManifest.xml 文件 <manifest> 标签下的 versionName 属性
     *
     * @return 返回版本字符串，比如：1.0
    </manifest> */
    val versionName: String
        get() = defPackageInfo!!.versionName

    /**
     * 取得版本号，对应 AndroidManifest.xml 文件 <manifest> 标签下的 versionCode 属性
     *
     * @return 返回版本，比如：1
    </manifest> */
    val versionCode: Int
        get() = defPackageInfo!!.versionCode

    // getPackageName()是你当前类的包名，0代表是获取版本信息
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

    val curProcessName: String?
        get() {
            val pid = android.os.Process.myPid()
            val mActivityManager = BaseApp.getInstance().getSystemService(
                    Context.ACTIVITY_SERVICE) as ActivityManager
            for (appProcess in mActivityManager.runningAppProcesses) {
                if (appProcess.pid == pid) {
                    return appProcess.processName
                }
            }
            return null
        }

    val appLabel: String?
        get() {
            val packageManager = BaseApp.getInstance().packageManager
            try {
                return packageManager.getApplicationLabel(
                        packageManager.getApplicationInfo(BaseApp.getInstance().packageName,
                                0)).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                LogUtil.e(TAG, "getDefPackageInfo: " + e.message)
            }

            return null
        }

    fun shareTextIntent(text: String): Intent {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        sendIntent.type = "text/plain"
        return sendIntent
    }

    fun getTextReceivers(pm: PackageManager): List<ResolveInfo> {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"

        return pm.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY)
    }

    fun isAppExist(packageName: String): Boolean {
        val packageManager = BaseApp.getInstance().packageManager
        // 获取所有已安装程序的包信息
        val pinfo = packageManager.getInstalledPackages(0)
        for (i in pinfo.indices) {
            if (pinfo[i].packageName.equals(packageName, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    fun gotoAppDetailsSettings(context: Context, requestCode: Int) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", context.packageName, null)
        if (context is Activity) {
            context.startActivityForResult(intent, requestCode)
        }
    }

    fun startActivityForPackage(context: Context, packageName: String): Boolean {
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

    /**
     * 打开其他的Activity，并附带Object
     *
     * @param cls   要打开的Activity
     * @param value 要附带的Object
     */
    //    public static void startActivity(Context context, Class<?> cls, String name, Object value)
    //    {
    //        Intent intent = new Intent(context, cls);
    //        intent.putExtra(name, PG.convertParcelable(value));
    //        context.startActivity(intent);
    //    }
    //
    //    public static void startActivity(Context context, Class<?> cls, Object value)
    //    {
    //        Intent intent = new Intent(context, cls);
    //        intent.putExtra(Constants.EXTRA_PARCELABLE, PG.convertParcelable(value));
    //        context.startActivity(intent);
    //    }
    //
    //    public static void startActivity(Context context, Intent intent, String name, Object value)
    //    {
    //        intent.putExtra(name, PG.convertParcelable(value));
    //        context.startActivity(intent);
    //    }
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

    fun startActivityForResult(context: Activity, cls: Class<*>, requestCode: Int,
                               name: String, value: String) {
        val intent = Intent(context, cls)
        intent.putExtra(name, value)
        context.startActivityForResult(intent, requestCode)
    }
}
