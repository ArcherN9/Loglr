package com.tumblr.loglr

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.util.Log
import android.view.*
import com.tumblr.loglr.Exceptions.LoglrAPIException
import com.tumblr.loglr.Exceptions.LoglrCallbackException
import com.tumblr.loglr.Exceptions.LoglrLoginCanceled
import com.tumblr.loglr.Exceptions.LoglrLoginException
import com.tumblr.loglr.Interfaces.DialogCallbackListener
import com.tumblr.loglr.Interfaces.DismissListener
import kotlinx.android.synthetic.main.fragment_tumblr_login.*

class LoglrFragment: DialogFragment(), DismissListener, DialogCallbackListener, DialogInterface.OnKeyListener {

    /**
     * The AsyncTask that initiates the login process by loading up Tumblr on the webview
     */
    private var taskTumblrLogin: TaskTumblrLogin? = null

    /**
     * The SMS broadcast receiver that receives text messages
     */
    private var otpBroadcastReceiver: OTPBroadcastReceiver? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity, theme) {

            override fun onBackPressed() {
                super.onBackPressed()
                //Pass reason for closing loglr
                val ex = LoglrLoginCanceled()
                Loglr.instance.exceptionHandler?.onLoginFailed(ex)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        //Remove the fragment title
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE);

        //Test if consumer key was received
        if (TextUtils.isEmpty(Loglr.instance.getConsumerKey()))
            throw LoglrAPIException()

        //Test if Secret Key was received
        if (TextUtils.isEmpty(Loglr.instance.getConsumerSecretKey()))
            throw LoglrAPIException()

        //Test if URL Call back was received
        if (TextUtils.isEmpty(Loglr.instance.getUrlCallBack()))
            throw LoglrCallbackException()

        return inflater?.inflate(R.layout.fragment_tumblr_login, container, false)
    }

    override fun onStart() {
        super.onStart()
        //Set custom height and width of webview
        val width: Int = resources.displayMetrics.widthPixels
        val height: Int = resources.displayMetrics.heightPixels
        dialog.window.setLayout(
                (width * 0.95).toInt(),
                (height * 0.80).toInt()
        );

        //Display a permission dialog if user is on Marshmallow and above
        if (Utils.isMarshmallowAbove() && !Utils.isSMSReadPermissionGranted(activity) && taskTumblrLogin == null) {
            val seekPermissionDialog: SeekPermissionDialog = SeekPermissionDialog(activity)
            seekPermissionDialog.setCanceledOnTouchOutside(false)
            seekPermissionDialog.setCancelable(false)
            seekPermissionDialog.setCallback(this)
            seekPermissionDialog.show()
        } else if(taskTumblrLogin == null)
            onButtonOkay()
    }

    override fun onDismiss() {
        try {
            activity.unregisterReceiver(otpBroadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace();
            //In all probability, exception will be thrown when there is nothing to unregister.
        } finally {
            dialog.dismiss()
        }
    }

    override fun onButtonOkay() {
        //Check if SMS read permissions have been granted to the application
        if(Utils.isSMSReadPermissionGranted(activity)) {
            //Register the SMS receiver
            registerReceiver()
            //test if LoginListener was registered
            if(Loglr.instance.loginListener != null) {
                if(Loglr.instance.exceptionHandler == null)
                    Log.w(TAG, "Continuing execution without ExceptionHandler. No Exception call backs will be sent. It is recommended to set one.");
                initiateLoginProcess()
            } else {
                //If Exception handler was registered by the dev, use it to return a call back.
                //Otherwise, just throw the exception and make the application crash
                if (Loglr.instance.exceptionHandler != null) {
                    val ex: LoglrLoginException = LoglrLoginException()
                    Loglr.instance.exceptionHandler?.onLoginFailed(ex)
                } else
                    throw LoglrLoginException()
            }
            //If not, Check if Its an android device that runs Marshmallow.
            //if it is, request user to grant permission
        } else if(Utils.isMarshmallowAbove()) {
            //Request user for permission.
            //Once granted or denied, callback will be on onRequestPermissionsResult
            ActivityCompat.requestPermissions(activity,
                    arrayOf(
                            Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_SMS
                    ),
                    OTPBroadcastReceiver.REQUEST_OTP_PERMISSION)
        } else
        //If permissions are not granted and its not Marshmallow, get on with login
            initiateLoginProcess()
    }

    override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
        //If the button tapped was back, exit the fragment
        if(event?.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss()
            return true
        } else
            return false
    }

    /**
     * Register the SMS OTP broadcast receiver
     */
    private fun registerReceiver(): Unit {
        //Initiate an AsyncTask to begin TumblrLogin
        taskTumblrLogin = TaskTumblrLogin()
        //Depending on whether device is running Kitkat or above,
        //Register broadcast receiver
        if(Utils.isKitkatAbove()) {
            otpBroadcastReceiver = OTPBroadcastReceiver()
            otpBroadcastReceiver?.setCallback(taskTumblrLogin!!)
            otpBroadcastReceiver?.setWebView(activity_tumblr_webview)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                activity.registerReceiver(otpBroadcastReceiver, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
            else
                activity.registerReceiver(otpBroadcastReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
        } else {
            otpBroadcastReceiver = OTPBroadcastReceiver()
            otpBroadcastReceiver?.setCallback(taskTumblrLogin!!)
            otpBroadcastReceiver?.setWebView(activity_tumblr_webview)
            activity.registerReceiver(otpBroadcastReceiver, IntentFilter(
                    resources.getString(R.string.tumblr_otp_provider)
            ))
        }
    }

    /**
     * A method to continue with the login process.
     */
    private fun initiateLoginProcess(): Unit {
        //Generate the loading dialog passed by the developer
        val dialog: Dialog = Utils.getLoadingDialog(activity)
        //Set a key listener on the dialog to keep a track of back buttons pressed in case the flow malfunctions
        dialog.setOnKeyListener(this@LoglrFragment)
        //Initiate an AsyncTask to begin Tumblr Login
        taskTumblrLogin = TaskTumblrLogin()
        //Pass context to AsyncTask
        taskTumblrLogin?.setContext(activity)
        //Pass Resources reference
        taskTumblrLogin?.setResources(resources)
        //Pass LoadingDialog as passed on by developer
        taskTumblrLogin?.setLoadingDialog(dialog)
        //Pass dismiss listener
        taskTumblrLogin?.setDismissListener(this@LoglrFragment)
        //Pass reference of WebView
        taskTumblrLogin?.setWebView(activity_tumblr_webview)
        //Execute AsyncTask
        taskTumblrLogin?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
        //Run case if permissions were requested for SMS read / write services
            OTPBroadcastReceiver.REQUEST_OTP_PERMISSION -> {
                var isGranted: Boolean = false
                //Run only when grant result and permissions have data in them.
                //Used to avoid a certain scenario when this method is auto executed even before the user accepts or denies permission request
                if (permissions.isNotEmpty() && grantResults.isNotEmpty()) {
                    for (intGrantResult in grantResults)
                    //if result is granted, set is granted to true
                        isGranted = intGrantResult == PackageManager.PERMISSION_GRANTED
                    //Test final grant status
                    if (isGranted)
                    //Register the SMS receiver
                        registerReceiver()

                    initiateLoginProcess()
                }
            }
        }
    }

    companion object {
        /**
         * A tag for logging
         */
        val TAG = LoglrFragment.javaClass.simpleName
    }
}