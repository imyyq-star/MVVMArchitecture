package com.imyyq.mvvm.base

import android.app.Activity
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.imyyq.mvvm.R
import com.imyyq.mvvm.utils.getViewBinding
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
         * [appBarLayoutRes] 通过 [DataBindingUtil] 将 xml 实例化成 binding，再和 [contentView]
         * 组合形成最终布局，如果为 null，那么将使用反射获取实例
         */
        fun <AppBarV : ViewDataBinding> inflateRootLayout(
            fragment: Fragment,
            contentView: View,
            appBarLayoutRes: Int?
        ): Pair<AppBarV, View> {
            return inflateRootLayout(fragment, fragment.requireActivity(), contentView, appBarLayoutRes)
        }

        fun <AppBarV : ViewDataBinding> inflateRootLayout(
            activity: Activity,
            contentView: View,
            appBarLayoutRes: Int?
        ): Pair<AppBarV, View> {
            return inflateRootLayout(activity, activity, contentView, appBarLayoutRes)
        }

        private fun <AppBarV : ViewDataBinding> inflateRootLayout(
            obj: Any,
            activity: Activity,
            contentView: View,
            appBarLayoutRes: Int?
        ): Pair<AppBarV, View> {
            // 实例化标题栏布局
            val appBarBinding: AppBarV = if (appBarLayoutRes != null) {
                DataBindingUtil.inflate(activity.layoutInflater, appBarLayoutRes, null, false)
            } else {
                obj.getViewBinding(activity.layoutInflater, 2)
            }

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
        ): View {
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