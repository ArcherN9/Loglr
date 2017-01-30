package com.tumblr.loglr;

import android.Manifest;
import android.app.Dialog;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.tumblr.loglr.Exceptions.LoglrAPIException;
import com.tumblr.loglr.Exceptions.LoglrCallbackException;
import com.tumblr.loglr.Exceptions.LoglrLoginCanceled;
import com.tumblr.loglr.Exceptions.LoglrLoginException;
import com.tumblr.loglr.Interfaces.DialogCallbackListener;
import com.tumblr.loglr.Interfaces.DismissListener;

public class LoglrFragment extends DialogFragment implements DismissListener, DialogCallbackListener {

    /**
     * A tag for logging
     */
    private static final String TAG = LoglrFragment.class.getSimpleName();

    /**
     * The AsyncTask that initiates the login process by loading up Tumblr on the webview
     */
    private TaskTumblrLogin taskTumblrLogin;

    /**
     * The SMS broadcast receiver that receives text messages
     */
    private OTPBroadcastReceiver otpBroadcastReceiver;

    /**
     * A bundle to store only login related params this bundle is sent alongwith the 'login' event
     */
    private Bundle loginBundle = new Bundle();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {

            @Override
            public void onBackPressed() {
                super.onBackPressed();
                //Pass reason for closing loglr
                if (Loglr.getInstance().getExceptionHandler() != null) {
                    if(Loglr.getInstance().getFirebase() != null)
                        Loglr.getInstance().getFirebase().logEvent(getString(R.string.FireBase_Event_LoginFailed), loginBundle);
                    Loglr.getInstance().getExceptionHandler().onLoginFailed(new LoglrLoginCanceled());
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //Remove the fragment title
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //Instantiate object & set to Loglr class
        Loglr.getInstance().setFirebase(FirebaseAnalytics.getInstance(getActivity()));
        //Set a new firebase user property to act as app version
        if(Loglr.getInstance().getFirebase() != null)
            Loglr.getInstance().getFirebase().setUserProperty(getString(R.string.FireBase_Property_Version), getString(R.string.FireBase_Property_Version_Value));
        //Send event for login button tap
        if(Loglr.getInstance().getFirebase() != null)
            Loglr.getInstance().getFirebase().logEvent(getString(R.string.FireBase_Event_ButtonClick), null);
        //Save param Fragment to login bundle
        loginBundle.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, getString(R.string.FireBase_Param_SignUp_Fragment));

        //Test if consumer key was received
        if (TextUtils.isEmpty(Loglr.getInstance().getConsumerKey()))
            throw new LoglrAPIException();

        //Test if Secret Key was received
        if (TextUtils.isEmpty(Loglr.getInstance().getConsumerSecretKey()))
            throw new LoglrAPIException();

        //Test if URL Call back was received
        if (TextUtils.isEmpty(Loglr.getInstance().getUrlCallBack()))
            throw new LoglrCallbackException();

        return inflater.inflate(R.layout.fragment_tumblr_login, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        //Set custom height and width of webview
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        getDialog().getWindow().setLayout(
                Double.valueOf(width * 0.95).intValue(),
                Double.valueOf(height * 0.80).intValue()
        );

        //Display a permission dialog if user is on Marshmallow and above
        if (Utils.isMarshmallowAbove() && !Utils.isSMSReadPermissionGranted(getActivity()) && taskTumblrLogin == null) {
            SeekPermissionDialog seekPermissionDialog = new SeekPermissionDialog(getActivity());
            seekPermissionDialog.setCanceledOnTouchOutside(false);
            seekPermissionDialog.setCancelable(false);
            seekPermissionDialog.setCallback(this);
            seekPermissionDialog.show();
        } else if(taskTumblrLogin == null)
            onButtonOkay();
    }

    /**
     * Register the SMS OTP broadcast receiver
     */
    private void registerReceiver() {
        //Initiate an AsyncTask to begin TumblrLogin
        taskTumblrLogin = new TaskTumblrLogin();
        //Depending on whether device is running Kitkat or above,
        //Register broadcast receiver
        if(Utils.isKitkatAbove()) {
            try {
                otpBroadcastReceiver = new OTPBroadcastReceiver();
                otpBroadcastReceiver.setCallback(taskTumblrLogin);
                otpBroadcastReceiver.setWebView((WebView) getView().findViewById(R.id.activity_tumblr_webview));
                getActivity().registerReceiver(otpBroadcastReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
            } catch (NullPointerException e) {
                e.printStackTrace();
                //NullPointer is thrown when getView().findViewById() is executed with getView() returning null
                //Usually happens when the dialog is closed and getView() is executed
                //If NullPointer is thrown, it implies BroadCast receiver did not receive the webview
                //Hence, no point of OTP receiver
                otpBroadcastReceiver = null;
            }
        } else {
            try {
                otpBroadcastReceiver = new OTPBroadcastReceiver();
                otpBroadcastReceiver.setCallback(taskTumblrLogin);
                otpBroadcastReceiver.setWebView((WebView) getView().findViewById(R.id.activity_tumblr_webview));
                getActivity().registerReceiver(otpBroadcastReceiver, new IntentFilter(
                        getResources().getString(R.string.tumblr_otp_provider)
                ));
            } catch (NullPointerException e) {
                e.printStackTrace();
                //NullPointer is thrown when getView().findViewById() is executed with getView() returning null
                //Usually happens when the dialog is closed and getView() is executed
                //If NullPointer is thrown, it implies BroadCast receiver did not receive the webview
                //Hence, no point of OTP receiver
                otpBroadcastReceiver = null;
            }
        }
    }

    /**
     * A method to continue with the login process.
     */
    private void initiateLoginProcess() {
        //Initiate an AsyncTask to begin Tumblr Login
        if(taskTumblrLogin == null)
            taskTumblrLogin = new TaskTumblrLogin();
        //Pass context to AsyncTask
        taskTumblrLogin.setContext(getActivity());
        //Pass Resources reference
        taskTumblrLogin.setResources(getResources());
        //Pass LoadingDialog as passed on by developer
        taskTumblrLogin.setLoadingDialog(Utils.getLoadingDialog(getActivity()));
        //Pass dismiss listener
        taskTumblrLogin.setDismissListener(LoglrFragment.this);
        //Pass reference of WebView
        taskTumblrLogin.setWebView(getView().findViewById(R.id.activity_tumblr_webview));
        //Pass the login bundle as well | Will be used when the login succeeds in the end
        taskTumblrLogin.setLoginBundle(loginBundle);
        //Execute AsyncTask
        taskTumblrLogin.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onDismiss() {
        try {
            getActivity().unregisterReceiver(otpBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
            //In all probability, exception will be thrown when there is nothing to unregister.
        } finally {
            getDialog().dismiss();
        }
    }

    @Override
    public void onButtonOkay() {
        //Check if SMS read permissions have been granted to the application
        if(Utils.isSMSReadPermissionGranted(getActivity())) {
            //Register the SMS receiver
            registerReceiver();
            //test if LoginListener was registered
            if(Loglr.getInstance().getLoginListener() != null) {
                if(Loglr.getInstance().getExceptionHandler() == null)
                    Log.w(TAG, "Continuing execution without ExceptionHandler. No Exception call backs will be sent. It is recommended to set one.");
                initiateLoginProcess();
            } else {
                //If Exception handler was registered by the dev, use it to return a call back.
                //Otherwise, just throw the exception and make the application crash
                if (Loglr.getInstance().getExceptionHandler() != null) {
                    if(Loglr.getInstance().getFirebase() != null)
                        Loglr.getInstance().getFirebase().logEvent(getString(R.string.FireBase_Event_LoginFailed), loginBundle);
                    Loglr.getInstance().getExceptionHandler().onLoginFailed(new LoglrLoginException());
                } else
                    throw new LoglrLoginException();
            }
            //If not, Check if Its an android device that runs Marshmallow.
            //if it is, request user to grant permission
        } else if(Utils.isMarshmallowAbove()) {
            //Request user for permission.
            //Once granted or denied, callback will be on onRequestPermissionsResult
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_SMS
                    },
                    OTPBroadcastReceiver.REQUEST_OTP_PERMISSION);
        } else
            //If permissions are not granted and its not Marshmallow, get on with login
            initiateLoginProcess();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            //Run case if permissions were requested for SMS read / write services
            case OTPBroadcastReceiver.REQUEST_OTP_PERMISSION:
                boolean isGranted = false;
                //Run only when grant result and permissions have data in them.
                //Used to avoid a certain scenario when this method is auto executed even before the user accepts or denies permission request
                if(permissions.length > 0 && grantResults.length > 0) {
                    for (int intGrantResult : grantResults)
                        //if result is granted, set is granted to true
                        isGranted = intGrantResult == PackageManager.PERMISSION_GRANTED;
                    //Test final grant status
                    if(isGranted) {
                        //Register the SMS receiver
                        registerReceiver();

                        Bundle bundle = new Bundle();
                        bundle.putBoolean(getString(R.string.FireBase_Param_UserGranted), true);
                        if (Loglr.getInstance().getFirebase() != null)
                            Loglr.getInstance().getFirebase().logEvent(getString(R.string.FireBase_Event_Read_Permission), bundle);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(getString(R.string.FireBase_Param_UserGranted), false);
                        if(Loglr.getInstance().getFirebase() != null)
                            Loglr.getInstance().getFirebase().logEvent(getString(R.string.FireBase_Event_Read_Permission), bundle);
                    }

                    initiateLoginProcess();
                }
                break;
        }
    }
}