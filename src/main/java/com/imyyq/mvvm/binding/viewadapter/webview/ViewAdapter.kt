package com.imyyq.mvvm.binding.viewadapter.webview

import android.webkit.WebView
import androidx.databinding.BindingAdapter

@BindingAdapter("render")
fun loadHtml(webView: WebView, html: String?) {
    html?.isNotEmpty()?.also {
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }
}