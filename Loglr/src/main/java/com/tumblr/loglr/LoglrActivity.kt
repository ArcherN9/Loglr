package com.tumblr.loglr

import android.Manifest
import android.annotation.TargetApi
import android.content.DialogInterface
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.widget.ImageView
import android.widget.TextView
import com.tumblr.loglr.Exceptions.LoglrAPIException
import com.tumblr.loglr.Exceptions.LoglrCallbackException
import com.tumblr.loglr.Exceptions.LoglrLoginCanceled
import com.tumblr.loglr.Exceptions.LoglrLoginException
import com.tumblr.loglr.Interfaces.DialogCallbackListener
import kotlinx.android.synthetic.main.activity_tumblr_login.*

class LoglrActivity: AppCompatActivity(), DialogCallbackListener, DialogInterface.OnKeyListener {

    /**
     * The AsyncTask that initiates the login process by loading up Tumblr on the webview
     */
    private var taskTumblrLogin: TaskTumblrLogin? = null

    /**
     * The OTP broadcast receiver that monitors for incoming SMS.
     */
    var otpBroadcastReceiver: OTPBroadcastReceiver? = null

    internal var txAddressbar: TextView? = null
    internal var btnClose: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tumblr_login)
        overridePendingTransition(R.anim.anim_bottom_up, R.anim.abc_fade_out)
        //Address bar as txAddressbar
        txAddressbar = activityTumblrAddressBar
        //Close button as btnClose
        btnClose = activityTumblrClose
        btnClose?.setOnClickListener { _ ->
            onBackPressed()
        }


        //Test if consumer key was received
        if(TextUtils.isEmpty(Loglr.Companion.instance.CONSUMER_KEY))
            throw LoglrAPIException()

        //Test if Secret Key was received
        if(TextUtils.isEmpty(Loglr.Companion.instance.CONSUMER_SECRET_KEY))
            throw LoglrAPIException()

        //Test if URL Call back was received
        if(TextUtils.isEmpty(Loglr.Companion.instance.strUrl))
            throw LoglrCallbackException()

        if(Loglr.Companion.instance.is2FAEnabled) {
            //Test if permissions will need to be sought for OTP
            if (Utils.Companion.isMarshmallowAbove() && !Utils.Companion.isSMSReadPermissionGranted(this@LoglrActivity)) {
                val seekPermissionDialog = SeekPermissionDialog(this@LoglrActivity)
                seekPermissionDialog.setCanceledOnTouchOutside(false)
                seekPermissionDialog.setCancelable(false)
                seekPermissionDialog.setCallback(this@LoglrActivity)
                seekPermissionDialog.show()
            } else
                onButtonOkay()
        } else
            onButtonOkay()
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
        //Run case if permissions were requested for SMS read / write services
            OTPBroadcastReceiver.REQUEST_OTP_PERMISSION -> {
                var isGranted = false
                //Run only when grant result and permissions have data in them.
                //Used to avoid a certain scenario when this method is auto executed even before the user accepts or denies permission request
                if(permissions.isNotEmpty() && grantResults.isNotEmpty()) {
                    for (intGrantResult in grantResults)
                    //if result is granted, set is granted to true
                        isGranted = intGrantResult == PackageManager.PERMISSION_GRANTED
                    //Test final grant status
                    if(isGranted)
                    //Register the SMS receiver
                        registerReceiver()
                    initiateLoginProcess()
                }
            }
        }
    }

    /**
     * A method to continue with the login process.
     */
    private fun initiateLoginProcess() {
        //Generate the loading dialog passed by the developer
        val dialog = Utils.getLoadingDialog(this@LoglrActivity)
        //Set a key listener on the dialog to keep a track of back buttons pressed in case the flow malfunctions
        dialog?.setOnKeyListener(this@LoglrActivity)
        //Initiate an AsyncTask to begin TumblrLogin
        if(taskTumblrLogin == null)
            taskTumblrLogin = TaskTumblrLogin()
        //Pass context to AsyncTask
        taskTumblrLogin!!.setContext(this@LoglrActivity)
        //Pass Resources reference
        taskTumblrLogin!!.setResources(resources)
        //Pass LoadingDialog as passed on by developer
        taskTumblrLogin!!.setLoadingDialog(dialog)
        //Pass reference of WebView
        taskTumblrLogin!!.setWebView(findViewById(R.id.activity_tumblr_webview))
        //Execute AsyncTask
        taskTumblrLogin!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    /**
     * Register the SMS OTP broadcast receiver
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun registerReceiver() {
        //Initiate an AsyncTask to begin TumblrLogin
        taskTumblrLogin = TaskTumblrLogin()
        //Depending on whether device is running Kitkat or above,
        //Register broadcast receiver
        if(Utils.Companion.isKitkatAbove()) {
            otpBroadcastReceiver = OTPBroadcastReceiver()
            otpBroadcastReceiver!!.setCallback(taskTumblrLogin!!)
            otpBroadcastReceiver!!.setWebView(activity_tumblr_webview)
            registerReceiver(otpBroadcastReceiver, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
        } else {
            otpBroadcastReceiver = OTPBroadcastReceiver()
            otpBroadcastReceiver!!.setCallback(taskTumblrLogin!!)
            registerReceiver(otpBroadcastReceiver, IntentFilter(
                    resources.getString(R.string.tumblr_otp_provider)
            ))
        }
    }

    override fun onButtonOkay() {
        //Test if 2FA has been enabled by the user at all
        if(Loglr.Companion.instance.is2FAEnabled)
        //Check if SMS read permissions have been granted to the application
            if(Utils.Companion.isSMSReadPermissionGranted(this@LoglrActivity)) {
                //Register the SMS receiver
                registerReceiver()
                //test if LoginListener was registered
                if(Loglr.Companion.instance.loginListener != null) {
                    if(Loglr.Companion.instance.exceptionHandler == null)
                        Log.w(TAG, "Continuing execution without ExceptionHandler. No Exception call backs will be sent. It is recommended to set one.");
                    initiateLoginProcess()
                } else {
                    //If Exception handler was registered by the dev, use it to return a call back.
                    //Otherwise, just throw the exception and make the application crash
                    val ex = LoglrLoginException()
                    Loglr.Companion.instance.exceptionHandler?.onLoginFailed(ex) ?: throw LoglrLoginException()
                }
                //If not, Check if Its an android device that runs Marshmallow.
                //if it is, request user to grant permission
            } else if(Utils.Companion.isMarshmallowAbove()) {
                //Request user for permission.
                //Once granted or denied, callback will be on onRequestPermissionsResult
                ActivityCompat.requestPermissions(this@LoglrActivity,
                        arrayOf(Manifest.permission.READ_SMS,
                                Manifest.permission.RECEIVE_SMS
                        ),
                        OTPBroadcastReceiver.REQUEST_OTP_PERMISSION)
            } else
            //If permissions are not granted and its not Marshmallow, get on with login
                initiateLoginProcess()
        else
        //If permissions are not granted and its not Marshmallow, get on with login
            initiateLoginProcess()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.abc_fade_in, R.anim.anim_up_bottom)
        //Pass reason for closing loglr
        val ex = LoglrLoginCanceled()
        Loglr.Companion.instance.exceptionHandler?.onLoginFailed(ex) ?: throw LoglrLoginException()
    }

    override fun finish() {
        super.finish();
        try {
            if(otpBroadcastReceiver != null)
                unregisterReceiver(otpBroadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
            //caught exceptions just in case
        }
    }

    override fun onKey(dialog: DialogInterface , keyCode: Int, event: KeyEvent):Boolean {
        //If the button tapped was back, exit the activity
        when(event.action) {
            KeyEvent.ACTION_UP -> onBackPressed()
            KeyEvent.KEYCODE_BACK -> onBackPressed()
            else -> return false
        }

        return true;
    }

    companion object {
        /**
         * A tag for logging
         */
        private val TAG = LoglrActivity.javaClass.simpleName
    }
}