package com.imyyq.mvvm.base

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewbinding.ViewBinding
import com.kingja.loadsir.callback.Callback

/**
 * 详见 [NoViewModelBaseActivity] 的注释，一样的
 */
abstract class AppBarNoViewModelBaseActivity<V : ViewBinding, AppBarV : ViewBinding> :
    AppBarViewBindingBaseActivity<V, BaseViewModel<BaseModel>, AppBarV>() {

    @SuppressLint("MissingSuperCall")
    final override fun initViewModel() {
    }

    final override fun isViewModelNeedStartAndFinish() = false

    final override fun isViewModelNeedStartForResult() = false

    final override fun isNeedLoadingDialog() = false

    @SuppressLint("MissingSuperCall")
    final override fun initLoadSir() {
    }

    final override fun getLoadSirTarget() = null

    @SuppressLint("MissingSuperCall")
    final override fun initUiChangeLiveData() {
    }

    @SuppressLint("MissingSuperCall")
    final override fun onCancelLoadingDialog() {
    }

    final override fun initViewModel(viewModelStoreOwner: ViewModelStoreOwner): BaseViewModel<BaseModel> {
        return super.initViewModel(viewModelStoreOwner)
    }

    final override fun showLoadingDialog(dialog: Dialog, msg: String?) {
    }

    final override fun dismissLoadingDialog(dialog: Dialog) {
    }

    final override fun loadingDialogLayout(): Int = 0

    final override fun loadingDialogLayoutMsgId(): Int = 0

    final override fun isLoadingDialogCancelable() = false

    final override fun isLoadingDialogCanceledOnTouchOutside() = false

    final override fun isCancelConsumingTaskWhenLoadingDialogCanceled() = false

    final override fun onLoadSirShowed(it: Class<out Callback>) {
    }

    final override fun onLoadSirSuccess() {
    }

    final override fun onLoadSirReload() {
    }
}