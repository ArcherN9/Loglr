package com.tumblr.loglr;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

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
     * Receives a reference of the interface to be called when a result is retrieved
     * from the login process
     * @param listener
     */
    public Loglr setLoginListener(@NonNull LoginListener listener) {
        loginListener = listener;
        return loglrInstance;
    }

    /**
     * Optional | Recommended though to handle code in a better fashion
     * The method receives a reference of the interface to be executed when an exception is thrown
     * @param listener
     */
    public Loglr setExceptionHandler(@NonNull ExceptionHandler listener) {
        exceptionHandler = listener;
        return loglrInstance;
    }

    /**
     * A call back URL to monitor for login call back
     * Should be same as callback URL registered with Tumblr website.
     * @param strUrl
     * @return
     */
    public Loglr setUrlCallBack(@NonNull String strUrl) {
        this.strUrl = strUrl;
        return loglrInstance;
    }

    /**
     * Accepts custom dialogs to replace with default ProgressDialogs whilst the
     * @param dialog A loading dialog to be shown to the user when data is loading from the server
     *               during sign in
     * @return LoglrInstance
     */
    public Loglr setLoadingDialog(Class<? extends Dialog> dialog) {
        this.loadingDialog = dialog;
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
     * A method to provide Loglr with the Consumer Key which will be used to access Tumblr APIs.
     * Without it, the app will fail.
     * #MANDATORY
     * @param strConsumerKey The Tumblr app consumer Key in String format
     * @return loglrInstance
     */
    public Loglr setConsumerKey(String strConsumerKey) {
        CONSUMER_KEY = strConsumerKey;
        return loglrInstance;
    }

    /**
     * A method to provide Loglr with the Consumer Secret Key which will be used to access Tumblr APIs.
     * Without it, the app will fail.
     * #MANDATORY
     * @param strConsumerSecretKey The Tumblr app consumer Secret Key in String format
     * @return loglrInstance
     */
    public Loglr setConsumerSecretKey(String strConsumerSecretKey) {
        CONSUMER_SECRET_KEY = strConsumerSecretKey;
        return loglrInstance;
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
     * The method initiates the login procedure by calling the Tumblr APIs in a different dialog Fragment
     * which hosts a WebView.
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