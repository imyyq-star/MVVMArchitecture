package com.imyyq.mvvm.binding.viewadapter.webview

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.BindingAdapter

@BindingAdapter("render")
fun loadHtml(webView: WebView, html: String?) {
    html?.isNotEmpty()?.also {
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }
}

@BindingAdapter("url")
fun loadUrl(webView: WebView, url: String?) {
    url?.isNotEmpty()?.also {
        webView.webViewClient = WebViewClient()
        webView.loadUrl(url)
    }
}