package com.imyyq.mvvm.utils

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ftd.livepermissions.LivePermissions
import com.ftd.livepermissions.PermissionResult
import com.imyyq.mvvm.app.BaseApp
import java.io.File

object CaptureAndCropManager {
    const val REQUEST_CODE_CAPTURE = 200
    const val REQUEST_CODE_CROP = 300
    const val REQUEST_CODE_ALBUM = 400

    val AUTHORITY = BaseApp.getInstance().packageName + ".genericFile.provider"

    // 获取最后一次拍照得到的照片
    var lastCameraCaptureImageFile: File? = null
        private set
    // 获取最后一次裁剪后的图片
    var lastCropImageFile: File? = null
        private set

    var outputX: Int? = null
    var outputY: Int? = null
    var aspectX: Int? = null
    var aspectY: Int? = null

    // 从相册获取照片完毕，从uri中跳到裁剪
    fun cropPhotoFromUri(activity: AppCompatActivity, uri: Uri, requestCode: Int) {
        cropPhotoFromUri(activity, null, uri, requestCode)
    }

    fun cropPhotoFromUri(fragment: Fragment, uri: Uri, requestCode: Int) {
        cropPhotoFromUri(null, fragment, uri, requestCode)
    }

    private fun cropPhotoFromUri(activity: AppCompatActivity?, fragment: Fragment?, uri: Uri, requestCode: Int) {
        lastCropImageFile = File(
            FileUtil.cacheDir,
                "crop_" + System.currentTimeMillis() + ".jpg")

        val intent = Intent("com.android.camera.action.CROP")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.setDataAndType(uri, "image/*")
        } else {
            intent.setDataAndType(uri, "image/*")
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(lastCropImageFile))
        // 设置裁剪
        fillCropIntent(intent)
        activity?.startActivityForResult(intent, requestCode)
                ?: fragment?.startActivityForResult(intent, requestCode)
    }

    // 拍照完毕，跳到裁剪
    fun cropPhotoAfterCapture(fragment: Fragment, requestCode: Int) {
        cropPhotoAfterCapture(null, fragment, requestCode)
    }

    fun cropPhotoAfterCapture(activity: AppCompatActivity, requestCode: Int) {
        cropPhotoAfterCapture(activity, null, requestCode)
    }

    private fun cropPhotoAfterCapture(activity: AppCompatActivity?, fragment: Fragment?, requestCode: Int) {
        lastCropImageFile = File(FileUtil.cacheDir,
                "crop_" + System.currentTimeMillis() + ".jpg")

        val intent = Intent("com.android.camera.action.CROP")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.setDataAndType(
                    FileProvider.getUriForFile(activity
                            ?: fragment!!.requireActivity(), AUTHORITY, lastCameraCaptureImageFile!!),
                    "image/*")
        } else {
            intent.setDataAndType(Uri.fromFile(lastCameraCaptureImageFile), "image/*")
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(lastCropImageFile))
        fillCropIntent(intent)
        activity?.startActivityForResult(intent, requestCode)
                ?: fragment?.startActivityForResult(intent, requestCode)
    }

    // 填充裁剪的Intent
    private fun fillCropIntent(intent: Intent) {
        // 设置裁剪
        intent.putExtra("crop", "true")
        // aspectX aspectY 是宽高的比例
        aspectX?.let {
            intent.putExtra("aspectX", it)
        }
        aspectY?.let {
            intent.putExtra("aspectY", it)
        }
        // outputX outputY 是裁剪图片宽高
        outputX?.let {
            intent.putExtra("outputX", it)
        }
        outputY?.let {
            intent.putExtra("outputY", it)
        }
        intent.putExtra("scale ", true)  //是否保留比例
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("return-data", true)
    }

    // 从相册获取照片
    fun capturePhotoFromGallery(context: AppCompatActivity, requestCode: Int) {
        capturePhotoFromGallery(context, null, requestCode)
    }

    fun capturePhotoFromGallery(fragment: Fragment, requestCode: Int) {
        capturePhotoFromGallery(null, fragment, requestCode)
    }

    fun capturePhotoFromGallery(activity: AppCompatActivity?, fragment: Fragment?, requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity?.startActivityForResult(intent, requestCode)
                ?: fragment?.startActivityForResult(intent, requestCode)
    }

    // 从相机获取照片
    fun capturePhotoFromCamera(context: AppCompatActivity, requestCode: Int) {
        capturePhotoFromCamera(context, null, requestCode)
    }

    fun capturePhotoFromCamera(fragment: Fragment, requestCode: Int) {
        capturePhotoFromCamera(null, fragment, requestCode)
    }

    private fun capturePhotoFromCamera(activity: AppCompatActivity?, fragment: Fragment?, requestCode: Int) {
        val lifecycleOwner = activity ?: fragment!!.activity as AppCompatActivity
        if (Utils.isNeedCheckPermission) {
            val live =  LivePermissions(lifecycleOwner)
            live.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).observe(lifecycleOwner, Observer {
                when (it) {
                    is PermissionResult.Grant -> {  //权限允许
                        captureCameraPhotoWithPermission(activity, fragment, requestCode)
                    }
                    is PermissionResult.Rationale -> {  //权限拒绝
                        ToastUtil.showLongToast("授权失败，无法使用相机")
                    }
                    is PermissionResult.Deny -> {   //权限拒绝，且勾选了不再询问
                        AppUtil.gotoAppDetailsSettings(BaseApp.getInstance(), 0)
                        ToastUtil.showLongToast("检测到您多次拒绝授权，请手动打开相机权限")
                    }
                }
            })
        } else {
            captureCameraPhotoWithPermission(activity, fragment, requestCode)
        }
    }

    // 必须有权限了，才可以拍照获取照片
    private fun captureCameraPhotoWithPermission(context: AppCompatActivity?, fragment: Fragment?, requestCode: Int) {
        lastCameraCaptureImageFile = File(FileUtil.cacheDir,
                "capture_" + System.currentTimeMillis() + ".jpg")

        val intentToTakePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageUri: Uri
        imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentToTakePhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            FileProvider.getUriForFile(context
                ?: fragment!!.requireActivity(), AUTHORITY, lastCameraCaptureImageFile!!)
        } else {
            Uri.fromFile(lastCameraCaptureImageFile)
        }
        //下面这句指定调用相机拍照后的照片存储的路径
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        // 格式
        intentToTakePhoto.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())

        if (intentToTakePhoto.resolveActivity(BaseApp.getInstance().packageManager) != null) {
            if (context != null) {
                context.startActivityForResult(intentToTakePhoto, requestCode)
            } else {
                fragment!!.startActivityForResult(intentToTakePhoto, requestCode)
            }
        }
    }
}
