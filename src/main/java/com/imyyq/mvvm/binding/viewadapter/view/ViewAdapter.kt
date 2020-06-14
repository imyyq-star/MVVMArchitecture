package com.imyyq.mvvm.binding.viewadapter.view

import android.view.View
import androidx.databinding.BindingAdapter

class ViewAdapter {
    companion object {

        @JvmStatic
        @BindingAdapter(value = ["onClickCommand", "isInterval"], requireAll = false)
        fun onClickCommand(
            view: View,
            clickCommand: View.OnClickListener,
            isInterval: Boolean
        ) {
            if (isInterval) {
                view.clickWithTrigger(800, clickCommand)
            } else {
                view.setOnClickListener(clickCommand)
            }
        }
    }
}