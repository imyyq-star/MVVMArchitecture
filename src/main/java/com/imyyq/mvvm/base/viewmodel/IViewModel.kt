package com.imyyq.mvvm.base.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver

/**
 * ViewModel 层，让 vm 可以感知 v 的生命周期
 *
 * @author imyyq.star@gmail.com
 */
interface IViewModel : DefaultLifecycleObserver