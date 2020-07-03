package com.imyyq.mvvm.base

/**
 * Model 层
 */
interface IModel {
    /**
     * 界面销毁时，会调用 vm 的 onCleared 方法，vm 会调用 m 的 onCleared 方法
     * vm 销毁时清除 m，与 vm 共消亡。m 层同样不能持有长生命周期对象
     */
    fun onCleared()
}