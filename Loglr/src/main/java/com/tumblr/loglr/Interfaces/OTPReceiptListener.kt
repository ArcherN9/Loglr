package com.tumblr.loglr.Interfaces

import android.webkit.WebView

interface OTPReceiptListener {

    fun onReceived(webView: WebView?, strOtp: String?)
}