package com.imyyq.mvvm.binding.viewadapter.checkbox;

import android.widget.CheckBox;

import androidx.databinding.BindingAdapter;

import com.imyyq.mvvm.binding.command.BindingCommand;

public class ViewAdapter {
    /**
     * @param bindingCommand //绑定监听
     */
    @BindingAdapter(value = {"onCheckedChangedCommand"}, requireAll = false)
    public static void setCheckedChanged(final CheckBox checkBox, final BindingCommand<Boolean> bindingCommand) {
        checkBox.setOnCheckedChangeListener((compoundButton, b) -> bindingCommand.execute(b));
    }
}
