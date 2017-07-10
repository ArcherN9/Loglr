package com.tumblr.loglr

import android.app.Dialog
import android.content.Context
import android.content.Intent
import com.tumblr.loglr.Interfaces.ExceptionHandler
import com.tumblr.loglr.Interfaces.LoginListener

//Empty private constructor to disallow creation of object
class Loglr private constructor() {

    /**
     * Specifies whether or not the developer wishes to enable 2FA auto read for OTP message that arrives
     * when the user is trying to login. Default value : true
     */
    internal var is2FAEnabled: Boolean = true

    /**
     * The Tumblr Call back URL
     */
    internal var strUrl: String? = null

    /**
     * The instance to this class. It is necessary to declare this here to keep access to this class singleton
     */
    private var loglrInstance: Loglr? = null

    /**
     * An object of the interface defined on this class. The interface is called
     * when the activity receives a response from the Login process
     */
    internal var loginListener: LoginListener? = null

    /**
     * An object of the interface defined on this class. The interface is called
     * when the activity throws an exception caused by various reasons due which
     * the code cannot continue function.
     */
    internal var exceptionHandler: ExceptionHandler? = null

    /**
     * The Consumer Key received when a new app is registered with Tumblr
     * #Mandatory
     */
    internal var CONSUMER_KEY: String? = null

    /**
     * The Consumer Secret Key received when a new app is registered with Tumblr
     * #Mandatory
     */
    internal var CONSUMER_SECRET_KEY: String? = null

    /**
     * A developer defined LoadingDialog which is shown to the user in cases when data is loading from
     * the server and the user is made to wait.
     * If no Dialog is passed to Loglr, default implementation is used with a ProgressDialog and
     * "loading..." as string.
     */
    private var loadingDialog: Class<out Dialog>? = null

    /**
     * A isDebug variable is used to specify if the build being run/deployed is a debug build or not.
     * On deployment this variable is to be changed to false to imply shift from debug to production
     */
    private val IS_DEBUG = true

    /**
     * Receives an interface to be called when login succeeds.
     * @param listener An implementation of the login listener which is called when the login succeeds
     */
    fun setLoginListener(listener: LoginListener): Loglr? {
        loginListener = listener
        return this@Loglr
    }

    /**
     * Optional | Recommended though to handle code in a better fashion
     * Receives an implementation of the interface to be executed when an exception is thrown and login fails to complete
     * @param listener An implementation of the ExceptionHandler interface that is called when login fails
     * *                 the details of which are held in the exception object
     * *
     * @return LoglrInstance
     */
    fun setExceptionHandler(listener: ExceptionHandler): Loglr? {
        exceptionHandler = listener
        return this@Loglr
    }

    /**
     * A call back URL to monitor for login call back
     * Should be same as callback URL registered with Tumblr website.
     * @param strUrl The url to which the user is redirected to when the login completes.
     * *
     * @return LoglrInstance
     */
    fun setUrlCallBack(strUrl: String): Loglr? {
        this.strUrl = strUrl
        return this@Loglr
    }

    /**
     * Accepts custom loading dialogs to replace with default ProgressDialogs. If you do not wish
     * to override login with custom loading dialogs, do not call this method.
     * @param dialog A loading dialog to be shown to the user when data is loading from the server
     * *               during sign in
     * *
     * @return LoglrInstance
     */
    fun setLoadingDialog(dialog: Class<out Dialog>): Loglr? {
        loadingDialog = dialog
        return this@Loglr
    }

    /**
     * A method to return the instance of loading dialog passed on by the developer |
     * To replace the default Dialog passed
     * @return
     */
    internal fun getLoadingDialog(): Class<out Dialog>? = loadingDialog

    /**
     * A method to return the URL call back registered with Tumblr on the developer dashboard
     * @return strUrl
     */
    internal fun getUrlCallBack(): String = strUrl!!

    /**
     * Provides Loglr with the Consumer Key which will be used to access Tumblr APIs.
     * Without it, the app will fail.
     * For more information on Tumblr Keys, please see : https://www.tumblr.com/docs/en/api/
     * #MANDATORY
     * @param strConsumerKey The Tumblr app consumer Key retrieved from Tumblr's developer website
     * @return loglrInstance
     */
    fun setConsumerKey(strConsumerKey: String): Loglr? {
        CONSUMER_KEY = strConsumerKey
        return this@Loglr
    }

    /**
     * Provides Loglr with the Consumer Secret Key which will be used to access Tumblr APIs.
     * Without it, the app will fail.
     * For more information on Tumblr Keys, please see : https://www.tumblr.com/docs/en/api/
     * #MANDATORY
     * @param strConsumerSecretKey The Tumblr app consumer Secret Key in String format
     * @return loglrInstance
     */
    fun setConsumerSecretKey(strConsumerSecretKey: String): Loglr? {
        CONSUMER_SECRET_KEY = strConsumerSecretKey
        return this@Loglr
    }

    /**
     * A toggle method to enable / disable the SMS OTP auto read functionality baked into Loglr.
     * Default value : true;
     * @param is2FAEnabled A boolean value that tells Loglr if OTP auto read assistance is to be
     * *                     enabled
     * *
     * @return LoglrInstance
     */
    fun enable2FA(is2FAEnabled: Boolean): Loglr? {
        this.is2FAEnabled = is2FAEnabled
        return this@Loglr
    }

    /**
     * A method to get the Consumer Key which will be used to access Tumblr APIs.
     * Without it, the app will fail.
     * #MANDATORY
     * @return CONSUMER_KEY
     * @java.lang.Deprecated Deprecated method. Will be removed in future builds
     */
    internal fun getConsumerKey(): String? = CONSUMER_KEY

    /**
     * A method to get the Consumer Secret Key which will be used to access Tumblr APIs.
     * Without it, the app will fail.
     * #MANDATORY
     * @return CONSUMER_SECRET_KEY
     */
    internal fun getConsumerSecretKey(): String? = CONSUMER_SECRET_KEY

    /**
     * Initiates the login procedure by calling calling the tumblr APIs in an activity that hosts
     * a web view.

     * Use this for a better experience of login in
     * @param context The context of the calling Activity / Application
     */
    fun initiateInActivity(context: Context) {
        val intent = Intent(context, LoglrActivity::class.java)
        context.startActivity(intent)
    }

    companion object {

        /**
         * A method to return a reference to this class. Since the variable
         * @return
         */
        val instance: Loglr get() {
            return loglrInstance
        }

        /**
         * The instance to this class. It is necessary to declare this here to keep access to this class singleton
         */
        private var loglrInstance: Loglr = Loglr()
    }
}