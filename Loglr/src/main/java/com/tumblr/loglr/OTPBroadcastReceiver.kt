package com.tumblr.loglr

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import android.webkit.WebView
import com.tumblr.loglr.Interfaces.OTPReceiptListener

class OTPBroadcastReceiver: BroadcastReceiver() {

    /**
     * The webview on which the OTP will be injected
     */
    private var webview: WebView? = null

    /**
     * An interface callback executed post reading OTP
     */
    private var otpReceiptListener: OTPReceiptListener? = null

    /**
     * A method to accept an interface callback that will be executed when the SMS is received and
     * parsed.
     * @param otpReceiptListener The interface to execute
     */
    internal fun setCallback(otpReceiptListener: OTPReceiptListener) {
        this@OTPBroadcastReceiver.otpReceiptListener = otpReceiptListener
    }

    /**
     * A method to pass on the WebView to the OTP broadcast receiver

     * @param webView A webview
     */
    internal fun setWebView(webView: WebView) {
        this@OTPBroadcastReceiver.webview = webView
    }

    @SuppressLint("NewApi")
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras
        var strMessage = ""
        val pdus = bundle?.get("pdus") as Array<*>
        val messages: ArrayList<Any> = arrayListOf<Any>(pdus)
        for(i in 0 until messages.size) {
            if (Utils.isMarshmallowAbove())
                messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, FORMAT_3GPP)
            else
                messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
            strMessage += (messages[i] as SmsMessage).messageBody
        }
        Log.i(TAG, strMessage)
        strMessage = strMessage.replace(Regex("\\D+"),"")
        Log.i(TAG, strMessage)
        otpReceiptListener?.onReceived(webview, strMessage)
    }

    companion object {
        /**
         * Tag for Logging
         */
        private val TAG: String = OTPBroadcastReceiver::class.java.simpleName

        /**
         * A request code to catch READ_SMS request response
         */
        val REQUEST_OTP_PERMISSION = 101

        /**
         * Indicates a 3GPP format SMS message.
         * @hide pending API council approval
         */
        val FORMAT_3GPP = "3gpp"
    }
}