package com.imyyq.mvvm.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.imyyq.mvvm.BuildConfig
import com.imyyq.mvvm.R
import com.imyyq.mvvm.app.BaseApp
import com.imyyq.mvvm.app.GlobalConfig
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*


/**
 * Created by 杨永青 on 16/9/13.
 * 可以通过AS的 File | Settings | Editor | General | Auto Import - Exclude from import and completion来禁用掉系统的Log类
 * 再通过 File | Settings | Editor | Live Templates，来设置快捷生成代码，可方便的访问此类
 */
object LogUtil {
    /**
     * 是否强制输出log，默认是 debug 输出log，可在其他构建类型，比如 Release 的时候，提供一个隐藏入口，比如点击某个地方 10 次打开 log 输出
     * 好处是可以在发布版本看到 log，可放心的在各个地方打 log
     */
    var mIsForceLog = false

    /**
     * Log 密码
     */
    var mLogPwd = if (BuildConfig.DEBUG) "12345678" else BaseApp.getInstance().packageName

    // 只有 debug 和 beta 才默认输出 log
    private val mIsLog: Boolean = BuildConfig.DEBUG || GlobalConfig.gIsBetaSaveLog

    private const val V = 0x1
    private const val D = 0x2
    private const val I = 0x3
    private const val W = 0x4
    private const val E = 0x5
    private const val A = 0x6

    @SuppressLint("HandlerLeak")
    private lateinit var mHandler: Handler
    private lateinit var mBufferWrite: BufferedWriter
    private lateinit var mCurDateTime: String

    @JvmStatic
    internal fun init() {
        CrashHandlerUtil.init()
    }

    @Synchronized
    private fun initLogHandler() {
        if (this::mHandler.isInitialized) {
            return
        }
        val handlerThread = HandlerThread("log")
        handlerThread.start()

        mCurDateTime = DateUtil.formatYMD_()
        mBufferWrite = BufferedWriter(
            OutputStreamWriter(
                FileOutputStream(
                    FileUtil.appLogDir + mCurDateTime + ".log",
                    true
                )
            )
        )
        mBufferWrite.write(getDeviceInfo())

        mHandler = object : Handler(handlerThread.looper) {
            override fun handleMessage(msg: Message) {
                val dateTime = DateUtil.formatYMD_()
                if (dateTime != mCurDateTime) {
                    if (this@LogUtil::mBufferWrite.isInitialized) {
                        mBufferWrite.close()
                    }
                    mCurDateTime = dateTime
                    mBufferWrite = BufferedWriter(
                        OutputStreamWriter(
                            FileOutputStream(
                                FileUtil.appLogDir + mCurDateTime + ".log",
                                true
                            )
                        )
                    )
                    mBufferWrite.write(getDeviceInfo())
                }

                mBufferWrite.write(DateUtil.formatYMDHMS_SSS() + "/" + msg.obj.toString())
                mBufferWrite.newLine()
                mBufferWrite.flush()
            }
        }
    }

    private fun getDeviceInfo(): String {
        val lineSeparator = "\r\n"
        val sb = StringBuilder()
        sb.append(lineSeparator)
        sb.append(lineSeparator)

        sb.append("appVerName:").append(AppUtil.versionName).append(lineSeparator)
        sb.append("appVerCode:").append(AppUtil.versionCode).append(lineSeparator)
        sb.append("buildType:").append(BuildConfig.BUILD_TYPE).append(lineSeparator)
        // 系统版本
        sb.append("OsVer:").append(Build.VERSION.RELEASE).append(lineSeparator)
        // 手机厂商
        sb.append("vendor:").append(Build.MANUFACTURER).append(lineSeparator)
        // 型号
        sb.append("model:").append(Build.MODEL).append(lineSeparator)
        sb.append(lineSeparator)
        sb.append(lineSeparator)
        return sb.toString()
    }

    @JvmStatic
    fun v(msg: Any?) {
        printLog(V, null, msg)
    }

    @JvmStatic
    fun v(tag: String?, msg: String?) {
        printLog(V, tag, msg)
    }

    @JvmStatic
    fun d(msg: Any?) {
        printLog(D, null, msg)
    }

    @JvmStatic
    fun d(tag: String?, msg: Any?) {
        printLog(D, tag, msg)
    }

    @JvmStatic
    fun i(msg: Any?) {
        printLog(I, null, msg)
    }

    @JvmStatic
    fun i(tag: String?, msg: Any?) {
        printLog(I, tag, msg)
    }

    @JvmStatic
    fun w(msg: Any?) {
        printLog(W, null, msg)
    }

    @JvmStatic
    fun w(tag: String?, msg: Any?) {
        printLog(W, tag, msg)
    }

    @JvmStatic
    fun e(msg: Any?) {
        printLog(E, null, msg)
    }

    @JvmStatic
    fun e(tag: String?, msg: Any?) {
        printLog(E, tag, msg)
    }

    @JvmStatic
    fun a(msg: Any?) {
        printLog(A, null, msg)
    }

    @JvmStatic
    fun a(tag: String?, msg: Any?) {
        printLog(A, tag, msg)
    }

    fun isLog() = mIsForceLog || mIsLog

    private fun printLog(type: Int, tagStr: String?, objectMsg: Any?) {
        if (!isLog()) {
            return
        }
        val stackTrace =
            Thread.currentThread().stackTrace
        val index = 4
        val className = stackTrace[index].fileName
        var methodName = stackTrace[index].methodName
        val lineNumber = stackTrace[index].lineNumber
        val tag = tagStr ?: className
        methodName = methodName.substring(0, 1).toUpperCase(Locale.getDefault()) + methodName.substring(1)
        val stringBuilder = StringBuilder()
        stringBuilder.append("[ (").append(className).append(":").append(lineNumber).append(")#")
            .append(methodName).append(" ] ")
        val msg = objectMsg?.toString() ?: "Log with null Object"
        stringBuilder.append(msg)
        val logStr = stringBuilder.toString()
        when (type) {
            V -> Log.v(tag, logStr)
            D -> Log.d(tag, logStr)
            I -> Log.i(tag, logStr)
            W -> Log.w(tag, logStr)
            E -> Log.e(tag, logStr)
            A -> Log.wtf(tag, logStr)
        }
        // 是否保存到本地缓存目录
        if (!this::mHandler.isInitialized && GlobalConfig.gIsBetaSaveLog) {
            initLogHandler()
        }
        // 已初始化，则可以发送消息了
        if (this::mHandler.isInitialized) {
            val m = Message.obtain()
            m.obj = "$tag = $logStr"
            mHandler.sendMessage(m)
        }
    }

    @JvmStatic
    fun getStackTraceString(t: Throwable): String {
        return Log.getStackTraceString(t)
    }

    /**
     * 通过多次的点击来打开 log，[frequency] 是点击的次数。
     * 连续点击达到点击次数后，弹出密码输入框，必须输入密码才可以打开 log
     */
    @JvmStatic
    fun multiClickToOpenLog(view: View, frequency: Int) {
        Utils.multiClickListener(view, frequency) {
            if (mIsForceLog && this::mHandler.isInitialized) {
                ToastUtil.showLongToast(BaseApp.getInstance().getString(R.string.log_already_open))
                return@multiClickListener
            }
            val activity = view.context as? Activity
            activity?.let {
                showPwdDialog(it)
            }
        }
    }

    private fun showPwdDialog(activity: Activity) {
        val layout = LinearLayout(activity)
        layout.orientation = LinearLayout.VERTICAL

        val edit = EditText(activity)
        val checkBox = CheckBox(activity)
        checkBox.text = activity.getString(R.string.save_to_local)

        layout.addView(edit)
        layout.addView(checkBox)

        val editDialog = AlertDialog.Builder(activity)
        editDialog.setTitle(activity.getString(R.string.please_input_pwd))
        editDialog.setView(layout)

        editDialog.setPositiveButton(R.string.confirm) { dialog, _ ->
            if (edit.text.isEmpty()) {
                ToastUtil.showShortToast(activity.getString(R.string.please_input_pwd))
                return@setPositiveButton
            }
            if (edit.text.toString() == mLogPwd) {
                ToastUtil.showShortToast(activity.getString(R.string.log_already_open2))
                mIsForceLog = true
                // 需要保存 log，则开启线程
                if (checkBox.isChecked) {
                    initLogHandler()
                }
            } else {
                ToastUtil.showShortToast(activity.getString(R.string.wrong_pwd))
            }
            dialog.dismiss()
        }

        editDialog.create().show()
    }
}
