package com.tumblr.loglr;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.tumblr.loglr.Interfaces.ExceptionHandler;
import com.tumblr.loglr.Interfaces.LoginListener;

public class Loglr {

    /**
     * The instance to this class. It is necessary to declare this here to keep access to this class singleton
     */
    private static Loglr loglrInstance;

    /**
     * An object of the interface defined on this class. The interface is called
     * when the activity receives a response from the Login process
     */
    private static LoginListener loginListener;

    /**
     * An object of the interface defined on this class. The interface is called
     * when the activity throws an exception caused by various reasons due which
     * the code cannot continue function.
     */
    private static ExceptionHandler exceptionHandler;

    /**
     * Specifies whether or not the developer wishes to enable 2FA auto read for OTP message that arrives
     * when the user is trying to login. Default value : true
     */
    private Boolean is2FAEnabled = true;

    /**
     * Firebase analytics object used module wide
     */
    private FirebaseAnalytics mFirebaseAnalytics;

    /**
     * The Tumblr Call back URL
     */
    private String strUrl;

    /**
     * The Consumer Key received when a new app is registered with Tumblr
     * #Mandatory
     */
    private static String CONSUMER_KEY;

    /**
     * The Consumer Secret Key received when a new app is registered with Tumblr
     * #Mandatory
     */
    private static String CONSUMER_SECRET_KEY;

    /**
     * A developer defined LoadingDialog which is shown to the user in cases when data is loading from
     * the server and the user is made to wait.
     * If no Dialog is passed to Loglr, default implementation is used with a ProgressDialog and
     * "loading..." as string.
     */
    private static Class<? extends Dialog> loadingDialog;

    /**
     * A isDebug variable is used to specify if the build being run/deployed is a debug build or not.
     * On deployment this variable is to be changed to false to imply shift from debug to production
     */
    private static Boolean IS_DEBUG = true;

    /**
     * The Dialog fragment serves as a second option to carry out the sign in procedure.
     */
    private LoglrFragment loglrFragment;

    private Loglr() {
        //Empty private constructor to disallow creation of object
    }

    /**
     * A method to return a reference to this class. Since the variable
     * @return
     */
    public static Loglr getInstance() {
        if(loglrInstance == null) {
            loglrInstance = new Loglr();
            return loglrInstance;
        } else
            return loglrInstance;
    }

    /**
     * A method to set the firebase analytics object to be available module wide to sent events
     * and params
     * @param mFirebaseAnalytics
     */
    void setFirebase(FirebaseAnalytics mFirebaseAnalytics) {
        this.mFirebaseAnalytics = mFirebaseAnalytics;
    }

    /**
     * Receives an interface to be called when login succeeds.
     * @param listener An implementation of the login listener which is called when the login succeeds
     */
    public Loglr setLoginListener(@NonNull LoginListener listener) {
        loginListener = listener;
        return loglrInstance;
    }

    /**
     * Optional | Recommended though to handle code in a better fashion
     * Receives an implementation of the interface to be executed when an exception is thrown and login fails to complete
     * @param listener An implementation of the ExceptionHandler interface that is called when login fails
     *                 the details of which are held in the exception object
     * @return LoglrInstance
     */
    public Loglr setExceptionHandler(@NonNull ExceptionHandler listener) {
        exceptionHandler = listener;
        return loglrInstance;
    }

    /**
     * A call back URL to monitor for login call back
     * Should be same as callback URL registered with Tumblr website.
     * @param strUrl The url to which the user is redirected to when the login completes.
     * @return LoglrInstance
     */
    public Loglr setUrlCallBack(@NonNull String strUrl) {
        this.strUrl = strUrl;
        return loglrInstance;
    }

    /**
     * Accepts custom loading dialogs to replace with default ProgressDialogs. If you do not wish
     * to override login with custom loading dialogs, do not call this method.
     * @param dialog A loading dialog to be shown to the user when data is loading from the server
     *               during sign in
     * @return LoglrInstance
     */
    public Loglr setLoadingDialog(@NonNull Class<? extends Dialog> dialog) {
        loadingDialog = dialog;
        return loglrInstance;
    }

    /**
     * A method to return the instance of loading dialog passed on by the developer |
     * To replace the default Dialog passed
     * @return
     */
    Class<? extends Dialog> getLoadingDialog() {
        return loadingDialog;
    }

    /**
     * A method to return the URL call back registered with Tumblr on the developer dashboard
     * @return strUrl
     */
    String getUrlCallBack() {
        return strUrl;
    }

    /**
     * Provides Loglr with the Consumer Key which will be used to access Tumblr APIs.
     * Without it, the app will fail.
     * #MANDATORY
     *
     * For more information on Tumblr Keys, please see : https://www.tumblr.com/docs/en/api/
     * @param strConsumerKey The Tumblr app consumer Key retrieved from Tumblr's developer website
     * @return loglrInstance
     */
    public Loglr setConsumerKey(String strConsumerKey) {
        CONSUMER_KEY = strConsumerKey;
        return loglrInstance;
    }

    /**
     * Provides Loglr with the Consumer Secret Key which will be used to access Tumblr APIs.
     * Without it, the app will fail.
     * #MANDATORY
     *
     * For more information on Tumblr Keys, please see : https://www.tumblr.com/docs/en/api/
     * @param strConsumerSecretKey The Tumblr app consumer Secret Key in String format
     * @return loglrInstance
     */
    public Loglr setConsumerSecretKey(String strConsumerSecretKey) {
        CONSUMER_SECRET_KEY = strConsumerSecretKey;
        return loglrInstance;
    }

    /**
     * A toggle method to enable / disable the SMS OTP auto read functionality baked into Loglr.
     * Default value : true;
     * @param is2FAEnabled A boolean value that tells Loglr if OTP auto read assistance is to be
     *                     enabled
     * @return LoglrInstance
     */
    public Loglr enable2FA(Boolean is2FAEnabled) {
        this.is2FAEnabled = is2FAEnabled;
        return loglrInstance;
    }

    /**
     * Returns whether Loglr can read the OTP or not
     * @return is2FAEnabled
     */
    Boolean is2FAEnabled() {
        return is2FAEnabled;
    }

    /**
     * Returns the firebase analytics object set up
     * @return mFirebaseAnalytics
     */
    FirebaseAnalytics getFirebase() {
        if(!IS_DEBUG)
            return this.mFirebaseAnalytics;
        else
            return null;
    }

    /**
     * A method to get the Consumer Key which will be used to access Tumblr APIs.
     * Without it, the app will fail.
     * #MANDATORY
     * @return CONSUMER_KEY
     */
    String getConsumerKey() {
        return CONSUMER_KEY;
    }

    /**
     * A method to get the Consumer Secret Key which will be used to access Tumblr APIs.
     * Without it, the app will fail.
     * #MANDATORY
     * @return CONSUMER_SECRET_KEY
     */
    String getConsumerSecretKey() {
        return CONSUMER_SECRET_KEY;
    }

    /**
     * Returns the reference of the interface to be called when a result is retrieved
     * from the Login Process
     * @return The Loginlistener
     */
    LoginListener getLoginListener() {
        return loginListener;
    }

    /**
     * Optional | Recommended though to handle code in a better fashion
     * The method returns a reference of the interface to be executed when an exception is thrown
     * @return The ExceptionHandler
     */
    ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Initiates the login procedure by calling calling the tumblr APIs in an activity that hosts
     * a web view.
     *
     * Use this for a better experience of login in
     * @param context The context of the calling Activity / Application
     */
    public void initiateInActivity(Context context) {
        Intent intent = new Intent(context, LoglrActivity.class);
        context.startActivity(intent);
    }

    /**
     * The method initiates the login procedure by calling the Tumblr APIs in a different dialog Fragment
     * which hosts a WebView.
     * @param fragmentManager The support fragment manager from the calling activity / application
     */
    public void initiateInDialog(FragmentManager fragmentManager) {
        //Instantiate Dialog Fragment
        loglrFragment = new LoglrFragment();
        //Show the dialogFragment
        loglrFragment.show(fragmentManager, LoglrFragment.class.getSimpleName());
    }

    /**
     * As ActivityCompat.requestPermissions() triggers a callback on the activity and not the fragment, it is
     * important to manually call the onRequestPermissionsResult() method on the fragment passing all
     * necessary parameters to the fragment from the parent activity.
     * This method is used only while using Loglr in Fragment mode and may be ignored if login is via Activity.
     * @param requestCode The Request code specified while requesting for SMS read permissions
     *                    Note : Do not modify
     * @param permissions The Permissions which were requested by the application
     *                    Note : Do not modify
     * @param grantResults The grant results array for each permission requested
     *                     Note : Do not modify
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(loglrFragment != null)
            loglrFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}