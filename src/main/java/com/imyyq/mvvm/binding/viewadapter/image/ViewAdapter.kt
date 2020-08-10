package com.imyyq.mvvm.binding.viewadapter.image

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.imyyq.mvvm.app.GlobalConfig

@SuppressLint("CheckResult")
@BindingAdapter(value = ["url", "placeholderRes", "errorRes"], requireAll = false)
fun setImageUri(
    imageView: ImageView,
    url: String?,
    placeholder: Drawable?,
    error: Drawable?
) {
    if (!TextUtils.isEmpty(url)) {
        //使用Glide框架加载图片
        val request = Glide.with(imageView.context)
            .load(url)
        val options = RequestOptions()

        if (placeholder != null) {
            options.placeholder(placeholder)
        } else {
            val placeholderRes = GlobalConfig.ImageView.placeholderRes
            placeholderRes?.let { options.placeholder(placeholderRes) }
        }
        if (error != null) {
            options.error(placeholder)
        } else {
            val errorRes = GlobalConfig.ImageView.errorRes
            errorRes?.let { options.error(errorRes) }
        }
        request.apply(options).into(imageView)
    } else {
        imageView.setImageResource(0)
    }
}