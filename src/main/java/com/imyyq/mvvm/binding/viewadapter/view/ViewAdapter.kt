package com.imyyq.mvvm.binding.viewadapter.view

import android.view.View
import androidx.databinding.BindingAdapter
import com.imyyq.mvvm.app.GlobalConfig

class ViewAdapter {
    companion object {

        @JvmStatic
        @BindingAdapter(value = ["onClickCommand", "isInterval", "intervalMilliseconds"], requireAll = false)
        fun onClickCommand(
            view: View,
            clickCommand: View.OnClickListener,
            // xml中没有配置，那么使用全局的配置
            isInterval: Boolean = GlobalConfig.mIsClickInterval,
            // 没有配置时间，使用全局配置
            intervalMilliseconds: Int = GlobalConfig.mClickIntervalMilliseconds
        ) {
            if (isInterval) {
                view.clickWithTrigger(intervalMilliseconds.toLong(), clickCommand)
            } else {
                view.setOnClickListener(clickCommand)
            }
        }
    }
}