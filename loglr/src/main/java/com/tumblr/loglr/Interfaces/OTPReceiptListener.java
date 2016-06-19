package com.tumblr.loglr.Interfaces;

import android.webkit.WebView;

public interface OTPReceiptListener {

    void onReceived(WebView webview, long lngOTP);
}
