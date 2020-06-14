package com.imyyq.mvvm.base

/**
 * 加载中接口
 */
interface ILoading {
    fun showResult()

    fun showLoading()

    fun showEmpty()

    fun showError()
}