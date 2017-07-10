package com.tumblr.loglr

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import com.tumblr.loglr.Exceptions.LoglrLoginException
import com.tumblr.loglr.Interfaces.DismissListener
import com.tumblr.loglr.Interfaces.OTPReceiptListener
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider
import oauth.signpost.exception.OAuthCommunicationException
import oauth.signpost.exception.OAuthExpectationFailedException
import oauth.signpost.exception.OAuthMessageSignerException
import oauth.signpost.exception.OAuthNotAuthorizedException

class TaskTumblrLogin: AsyncTask<Any, RuntimeException, String>(), OTPReceiptListener {

    /**
     * The OAuth provider
     */
    private var commonsHttpOAuthProvider: CommonsHttpOAuthProvider? = null

    /**
     * OAuth Consumer
     */
    private var commonsHttpOAuthConsumer: CommonsHttpOAuthConsumer? = null

    /**
     * A loading Dialog to display to user if passed by developer
     */
    private var loadingDialog: Dialog? = null

    /**
     * Android Resources used to access Strings.xml files
     */
    private var resources: Resources? = null

    /**
     * Context of the calling activity
     */
    private var context: Context? = null

    /**
     * The webview which opens the Tumblr Website
     */
    private var webView: WebView? = null

    /**
     * The OTP received from Tumblr
     */
    private var strOTP: String? = null

    /**
     * A dismiss listener is to be called in case the AsyncTask's calling context is that
     * of a dialogFragment
     */
    private var dismissListener: DismissListener? = null

    /**
     * A method to pass the dismiss listener to the AsyncTask. Is used exclusively by the
     * dialog Fragment
     * @param dismissListener A reference to the Dismiss listener interface
     */
    fun setDismissListener(dismissListener: DismissListener): Unit {
        this.dismissListener = dismissListener
    }

    /**
     * Accept Loading dialog passed on by the developer. Null in case no Dialog was passed
     * @param loadingDialog Loading Dialog to display to user
     */
    fun setLoadingDialog(loadingDialog: Dialog): Unit {
        this.loadingDialog = loadingDialog
    }

    /**
     * The Resources set method to give access to resources to the AsyncTask
     * @param resources resources from the Activity
     */
    fun setResources(resources: Resources): Unit {
        this.resources = resources
    }

    /**
     * Set the context to show progressBars and stuff
     * @param context
     */
    fun setContext(context: Context): Unit {
        this.context = context
    }

    /**
     * A method to pass a reference of the WebView to this AsyncTask class
     * @param view
     */
    fun setWebView(view: Any): Unit {
        this.webView = view as WebView
    }

    override fun onPreExecute() {
        super.onPreExecute()
        //If the developer a loading dialog, show that instead of default.
        loadingDialog?.show()
    }

    override fun doInBackground(vararg params: Any?): String {
        try {
            //Generate a new oAuthConsumer object
            commonsHttpOAuthConsumer = CommonsHttpOAuthConsumer(
                    Loglr.instance.getConsumerKey(),
                    Loglr.instance.getConsumerSecretKey()
            )
            //Generate a new oAuthProvider object
            commonsHttpOAuthProvider = CommonsHttpOAuthProvider(
                    resources?.getString(R.string.tumblr_request),
                    resources?.getString(R.string.tumblr_access),
                    resources?.getString(R.string.tumblr_auth)
            )
            //Retrieve the URL to which the user must be sent in order to authorize the consumer
            return commonsHttpOAuthProvider!!.retrieveRequestToken(
                    commonsHttpOAuthConsumer,
                    Loglr.instance.getUrlCallBack()
            )
        } catch (e: OAuthMessageSignerException) {
            e.printStackTrace()
            publishProgress(LoglrLoginException(e.message!!))
            return ""
        } catch (e: OAuthNotAuthorizedException) {
            e.printStackTrace()
            publishProgress(LoglrLoginException(e.message!!))
            return ""
        } catch (e: OAuthExpectationFailedException) {
            e.printStackTrace()
            publishProgress(LoglrLoginException(e.message!!))
            return ""
        } catch (e: OAuthCommunicationException) {
            e.printStackTrace()
            publishProgress(LoglrLoginException(e.message!!))
            return ""
        }
    }

    override fun onProgressUpdate(vararg values: RuntimeException) {
        super.onProgressUpdate(values[0])
        if(values.isNotEmpty()) {
            val exception: RuntimeException = values[0]
            Loglr.instance.exceptionHandler?.onLoginFailed(exception)
            finish()
        } else
            finish()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onPostExecute(strAuthUrl: String?) {
        super.onPostExecute(strAuthUrl)
        //Dismiss progress bar
        loadingDialog?.dismiss()

        if(!TextUtils.isEmpty(strAuthUrl)) {
            //Enable JS support on web browser - important since TumblrLogin utilises JS components
            //Login page will not show up properly if this is not done
            webView?.settings?.javaScriptEnabled = true
            //Set a web view client to monitor browser interactions
            webView?.setWebViewClient(object: WebViewClient() {

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    if(!TextUtils.isEmpty(url)
                            && url.equals(context?.resources?.getString(R.string.tumblr_auth_otp_redirect), ignoreCase = false)
                            && !TextUtils.isEmpty(strOTP)
                            && !strOTP.equals((0).toString(), ignoreCase = false))
                        webView?.loadUrl("javascript:document.getElementById(\"tfa_response_field\").value=$strOTP;")
                }

                @Suppress("OverridingDeprecatedMember")
                override fun shouldOverrideUrlLoading(view: WebView, strUrl: String): Boolean {
                    //Log Current loading URL
                    Log.i(TAG, strUrl)
                    //Check if the Currently loading URL is that of the call back URL mentioned on top
                    if (strUrl.toLowerCase().contains(Loglr.instance.getUrlCallBack().toLowerCase())) {
                        //Parse string URL to conver to URI
                        val uri : Uri = Uri.parse(strUrl)
                        //instantiate String variables to store OAuth & Verifier tokens
                        var strOAuthToken: String = ""
                        var strOAuthVerifier : String = ""
                        //Iterate through Parameters retrieved on the URL
                        for (strQuery in uri.queryParameterNames)
                            when (strQuery) {
                            //Save OAuth Token
                            //Note : This is not the login token we require to set on JumblrToken
                                "oauth_token" -> strOAuthToken = uri.getQueryParameter(strQuery)
                            //Save OAuthVerifier
                                "oauth_verifier" -> strOAuthVerifier = uri.getQueryParameter(strQuery)
                            }
                        //Execute a new AsyncTask to retrieve access tokens
                        //Performing this is important since communication using OAuthProvider
                        //can only be done in a background thread.
                        val taskRetrieveAccessToken: TaskRetrieveAccessToken = TaskRetrieveAccessToken()
                        //Pass OAuthConsumer as an argument
                        taskRetrieveAccessToken.setOAuthConsumer(commonsHttpOAuthConsumer)
                        //Pass OAuthProvider as an argument
                        taskRetrieveAccessToken.setOAuthProvider(commonsHttpOAuthProvider)
                        //Pass context to AsyncTask
                        taskRetrieveAccessToken.setContext(context)
                        //Pass Loading Dialog
                        taskRetrieveAccessToken.setLoadingDialog(loadingDialog)
                        //Pass OAuthVerifier as an argument
                        taskRetrieveAccessToken.setOAuthVerifier(strOAuthVerifier)
                        //Set the Dismiss listener
                        taskRetrieveAccessToken.setDismissListener(dismissListener)
                        //Execute the AsyncTask on a different thread;
                        taskRetrieveAccessToken.executeOnExecutor(THREAD_POOL_EXECUTOR)
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, strUrl)
                }
            })

            //Load URL
            webView?.loadUrl(strAuthUrl)
        } else
            finish()
    }

    /**
     * A method to finish the calling activity
     */
    private fun finish() {
        try {
            val loglrActivity: LoglrActivity = context as LoglrActivity
            loglrActivity.finish()
        } catch (e: ClassCastException) {
            //Class cast exception is thrown when the container activity is not
            //LoglrActivity. In such a scenario, it is obvious that Loglr
            //was called using the dialogFragment. In this case, we dismiss the fragment
            //Not printing the stacktrace so it does not go to dev's console
            dismissListener?.onDismiss()
        }
    }

    override fun onReceived(webView: WebView?, strOtp: String?) {
        Log.i(TAG, "OTP Received to populate WebView : " + strOTP)
        this.strOTP = strOTP
        webView?.loadUrl("javascript:document.getElementById(\"tfa_response_field\").value=$strOTP;")
    }

    companion object {
        /**
         * Tag for logging
         */
        private val TAG:String = TaskTumblrLogin::class.java.simpleName
    }
}