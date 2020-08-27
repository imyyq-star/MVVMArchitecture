package com.imyyq.mvvm.binding.viewadapter.view

import android.view.View
import androidx.databinding.BindingAdapter
import com.imyyq.mvvm.app.GlobalConfig
import com.imyyq.mvvm.http.HttpRequest
import com.imyyq.mvvm.utils.LogUtil

@BindingAdapter(
    value = ["onClickCommand", "isInterval", "intervalMilliseconds"],
    requireAll = false
)
fun onClickCommand(
    view: View,
    clickCommand: View.OnClickListener?,
    isInterval: Boolean?,
    intervalMilliseconds: Int?
) {
    var interval = isInterval
    // xml中没有配置，那么使用全局的配置
    if (interval == null) {
        interval = GlobalConfig.Click.gIsClickInterval
    }
    // 没有配置时间，使用全局配置
    var milliseconds = intervalMilliseconds
    if (milliseconds == null) {
        milliseconds = GlobalConfig.Click.gClickIntervalMilliseconds
    }
    if (interval) {
        clickCommand?.let { view.clickWithTrigger(milliseconds.toLong(), it) }
    } else {
        view.setOnClickListener(clickCommand)
    }
}

@BindingAdapter(
    value = ["multiClickToOpenLog"]
)
fun multiClickToOpenLog(
    view: View,
    frequency: Int?
) {
    frequency?.let { LogUtil.multiClickToOpenLog(view, it) }
}

@BindingAdapter(
    value = ["multiClickToChangeBaseUrl"]
)
fun multiClickToChangeBaseUrl(
    view: View,
    frequency: Int?
) {
    frequency?.let { HttpRequest.multiClickToChangeBaseUrl(view, it) }
}