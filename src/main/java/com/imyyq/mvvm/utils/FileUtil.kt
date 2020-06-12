package com.imyyq.mvvm.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.imyyq.mvvm.app.BaseApp
import org.json.JSONException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

object FileUtil {

    val cacheDir: File
        get() {
            return BaseApp.getInstance().externalCacheDir ?: return BaseApp.getInstance().cacheDir
        }

    // 取得App的文件目录，在sd卡根目录下用包名为目录名，或者在内部的存储目录，用/结尾
    val appDir: String
        get() = if (isSDCardMounted)
            sdCardBaseDir + "/" + AppUtil.defPackageInfo!!.packageName + "/"
        else
            BaseApp.getInstance().filesDir.absolutePath + "/"

    // 取得App的文件目录，/sdcard/包名/File，没有sd卡则是/data/data/包名/files/File
    val appFileDir: String
        get() {
            val s = "/File/"
            return if (isSDCardMounted)
                sdCardBaseDir + "/" + AppUtil.defPackageInfo!!.packageName + s
            else
                BaseApp.getInstance().filesDir.absolutePath + s
        }

    // 取得App的log目录：/sdcard/包名/Log，没有sd卡则是/data/data/包名/files/Log
    val appLogDir: String
        get() {
            val s = "/Log/"
            val path = if (isSDCardMounted)
                BaseApp.getInstance().externalCacheDir?.absolutePath + s
            else
                BaseApp.getInstance().filesDir.absolutePath + s
            val file = File(path)
            if (!file.exists()) {
                file.mkdirs()
            }
            return path
        }

    // 判断SD卡是否被挂载
    // return Environment.getExternalStorageState().equals("mounted");
    val isSDCardMounted: Boolean
        get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    // 获取SD卡的根目录
    val sdCardBaseDir: String?
        get() = if (isSDCardMounted) {
            Environment.getExternalStorageDirectory().absolutePath
        } else null

    /**
     * 获取内部存储Log文件夹路径,比如
     * /data/user/0/org.sxisa.ui/files/Log/
     */
    val internalLogDir: String
        get() = getInternalFilesDir("Log")

    private val isSDCardExists: Boolean
        get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

    fun getDirLengthForM(dir: File): Double {
        return getDirLength(dir) * 1.0 / 1024.0 / 1024.0
    }

    fun getDirLength(dir: File): Long {
        var length: Long = 0
        // 删除文件夹中的所有文件包括子目录
        val files = dir.listFiles() ?: return length
        for (file in files) {
            // 删除子文件
            if (file.isFile) {
                length += file.length()
            } else if (file.isDirectory) {
                length += getDirLength(file)
            }// 删除子目录
        }
        return length
    }

    fun readAssetsFileContent(fileName: String): String? {
        try {
            BaseApp.getInstance().assets.open(fileName).use { stream ->
                val bs = ByteArray(stream.available())
                stream.read(bs)
                return String(bs)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 保存字符串到本地
     */
    private fun saveStrToLocal(filePath: String, str: String, append: Boolean): Boolean {
        val file = File(filePath)
        if (!file.parentFile.exists())
        // 文件路径不存在则创建
        {
            if (!file.parentFile.mkdirs())
            // 创建失败直接返回
            {
                return false
            }
        }
        var fw: FileOutputStream? = null
        try {
            fw = FileOutputStream(filePath, append)
            fw.write(str.toByteArray(charset("UTF-8")))
            return true
        } catch (ex: IOException) {
            ex.printStackTrace()
        } catch (ex: JSONException) {
            ex.printStackTrace()
        } finally {
            try {
                if (fw != null) {
                    fw.flush()
                    fw.close()
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

        }
        return false
    }

    /**
     * 保存字符串到内部的存储
     */
    fun saveStr2Internal(filePath: String, str: String, append: Boolean): Boolean {
        return saveStrToLocal(filePath, str, append)
    }

    /**
     * 保存字符串到外部存储
     */
    fun saveStr2External(filePath: String, str: String, append: Boolean): Boolean {
        return isSDCardExists && saveStrToLocal(filePath, str, append)
    }

    /**
     * @param rootDir 根目录,比如/sdcard
     * @param dirName 子目录,可以多级,比如com.yyq,Log
     * @return 比如/sdcard/com.yyq/Log/
     */
    private fun getDir(rootDir: String, vararg dirName: Any): String {
        val format = StringBuilder("/")
        for (i in dirName.indices) {
            format.append("%s/")
        }
        return String.format(Locale.getDefault(), rootDir + format.toString(), *dirName)
    }

    /**
     * 获取外部存储文件夹路径,比如参数是:org.sxisa.ui,Log,那么返回的是
     * /sdcard/org.sxisa.ui/Log/
     */
    fun getExternalDir(vararg dirName: Any): String {
        return getDir(Environment.getExternalStorageDirectory().absolutePath, *dirName)
    }

    /**
     * 获取内部存储files文件夹路径,比如输入的是Log
     * /data/user/0/org.sxisa.ui/files/Log/
     */
    fun getInternalFilesDir(vararg dirName: Any): String {
        return getDir(BaseApp.getInstance().filesDir.absolutePath, *dirName)
    }

    fun getFilePathFromUri(context: Context, uri: Uri?): String? {
        if (null == uri) {
            return null
        }
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null) {
            data = uri.path
        } else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = context.contentResolver.query(uri,
                    arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        } else {
            LogUtil.e("FileUtil", "getFilePathFromUri: $uri")
        }
        return data
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    fun delete(fileName: String): Boolean {
        val file = File(fileName)
        return if (!file.exists()) {
            false
        } else {
            if (file.isFile) {
                deleteFile(fileName)
            } else {
                deleteDirectory(fileName, true)
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    fun deleteFile(fileName: String): Boolean {
        val file = File(fileName)
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        return if (file.exists() && file.isFile) {
            file.delete()
        } else {
            false
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    fun deleteDirectory(dir: String, isDeleteSelf: Boolean): Boolean {
        val dirFile = File(dir)
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory) {
            return false
        }
        // 删除文件夹中的所有文件包括子目录
        val files = dirFile.listFiles()
        for (file in files) {
            // 删除子文件
            if (file.isFile) {
                deleteFile(file.absolutePath)
            } else if (file.isDirectory) {
                deleteDirectory(file.absolutePath, true)
            }// 删除子目录
        }
        // 删除当前目录
        return isDeleteSelf && dirFile.delete()
    }
}
