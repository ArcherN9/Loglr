package com.tumblr.loglr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.webkit.WebView;

import com.tumblr.loglr.Interfaces.OTPReceiptListener;

public class OTPBroadcastReceiver extends BroadcastReceiver {

    /**
     * Tag for Logging
     */
    private static final String TAG = OTPBroadcastReceiver.class.getSimpleName();

    /**
     * A request code to catch READ_SMS request response
     */
    static final int REQUEST_OTP_PERMISSION = 101;

    /**
     * Indicates a 3GPP format SMS message.
     * @hide pending API council approval
     */
    private static final String FORMAT_3GPP = "3gpp";

    /**
     * The webview on which the OTP will be injected
     */
    private WebView webview;

    /**
     * An interface callback executed post reading OTP
     */
    private OTPReceiptListener otpReceiptListener;

    /**
     * A method to accept an interface callback that will be executed when the SMS is received and
     * parsed.
     * @param otpReceiptListener The interface to execute
     */
    void setCallback(OTPReceiptListener otpReceiptListener) {
        this.otpReceiptListener = otpReceiptListener;
    }

    /**
     * A method to pass on the WebView to the OTP broadcast receiver
     *
     * @param webView A webview
     */
    void setWebView(WebView webView) {
        this.webview = webView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages;
        String strMessage = "";
        if(bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if(pdus != null) {
                messages = new SmsMessage[pdus.length];
                for (int i = 0; i < messages.length; i++) {
                    if (Utils.isMarshmallowAbove())
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], FORMAT_3GPP);
                    else
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    strMessage += messages[i].getMessageBody();
                }
                Log.i(TAG, strMessage);
                strMessage = strMessage.replaceAll("\\D+","");
                Log.i(TAG, strMessage);
                otpReceiptListener.onReceived(webview, strMessage);
            }
        }
    }
}