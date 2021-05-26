package com.imyyq.mvvm.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * 打开应用的详细信息设置
 */
fun Context.gotoAppDetailsSettings(requestCode: Int, packageName: String) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.fromParts("package", packageName, null)
    (this as Activity).startActivityForResult(intent, requestCode)
}

/**
 * 效果和在 Launcher 应用点击图标启动是一样的
 */
fun Context.startActivityInNewTask(packageName: String, activityName: String) {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
    intent.component = ComponentName(packageName, activityName)
    startActivity(intent)
}

/**
 * 打开指定包名的应用
 */
fun Context.startActivityByPackage(packageName: String) {
    val pm = packageManager
    val intent = pm.getLaunchIntentForPackage(packageName)
    startActivity(intent)
}

/**
 * 打开其他的Activity
 *
 * @param cls 要打开的Activity
 */
fun Context.startActivity(cls: Class<out Activity>) {
    startActivity(Intent(this, cls))
}

fun Context.startActivity(cls: Class<out Activity>, name: String, value: Int) {
    val intent = Intent(this, cls)
    intent.putExtra(name, value)
    startActivity(intent)
}

fun Context.startActivity(cls: Class<out Activity>, name: String, value: Boolean) {
    val intent = Intent(this, cls)
    intent.putExtra(name, value)
    startActivity(intent)
}

/**
 * 打开其他的Activity，并附带字符串
 *
 * @param cls   要打开的Activity
 * @param name  字符串名称
 * @param value 字符串值
 */
fun Context.startActivity(cls: Class<out Activity>, name: String, value: String) {
    val intent = Intent(this, cls)
    intent.putExtra(name, value)
    startActivity(intent)
}

fun Context.startActivityForResult(
    cls: Class<out Activity>, requestCode: Int,
    name: String, value: String
) {
    val intent = Intent(this, cls)
    intent.putExtra(name, value)
    (this as Activity).startActivityForResult(intent, requestCode)
}