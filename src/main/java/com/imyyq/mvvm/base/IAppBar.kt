package com.imyyq.mvvm.base

import android.app.Activity
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.imyyq.mvvm.R
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

interface IAppBar<AppBarP : IAppBarProcessor> {
    /**
     * 初始化 AppBar 的处理者
     */
    @Suppress("UNCHECKED_CAST")
    fun initAppBarProcessor(): AppBarP {
        val modelClass: Class<AppBarP>?
        val type: Type? = javaClass.genericSuperclass
        modelClass = if (type is ParameterizedType) {
            type.actualTypeArguments[3] as? Class<AppBarP>
        } else null
        if (modelClass == null) {
            throw RuntimeException("必须指定 AbsAppBarProcessor ")
        }
        return modelClass.newInstance()
    }

    companion object {
        /**
         * [appBarLayoutId] 通过 [DataBindingUtil] 将 xml 实例化成 binding，再和 [contentView]
         * 组合形成最终布局
         */
        fun <AppBarV : ViewDataBinding> inflateRootLayout(
            activity: Activity,
            contentView: View,
            appBarLayoutId: Int
        ): Pair<AppBarV, LinearLayout> {
            // 实例化标题栏布局
            val appBarBinding: AppBarV =
                DataBindingUtil.inflate(activity.layoutInflater, appBarLayoutId, null, false)
            val linear = generateRootLayout(activity, appBarBinding, contentView)
            return Pair(appBarBinding, linear)
        }

        /**
         * 将 [appBarBinding] 和 [contentView] 通过 LinearLayout 组合起来形成最终布局
         */
        fun generateRootLayout(
            activity: Activity,
            appBarBinding: ViewBinding,
            contentView: View
        ): LinearLayout {
            val linear = LinearLayout(activity)
            linear.orientation = LinearLayout.VERTICAL
            linear.addView(appBarBinding.root)
            // 已有的布局撑满剩下的空间
            val param = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1F)
            contentView.layoutParams = param
            linear.addView(contentView)

            // 处理返回按钮
            appBarBinding.root.findViewById<View>(R.id.commonAppBarBackBtnId)
                ?.setOnClickListener { activity.finish() }

            return linear
        }
    }
}