package com.tumblr.loglr

import android.app.Dialog
import android.content.Context
import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log
import com.tumblr.loglr.Exceptions.LoglrLoginException
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider
import oauth.signpost.exception.OAuthCommunicationException
import oauth.signpost.exception.OAuthExpectationFailedException
import oauth.signpost.exception.OAuthMessageSignerException
import oauth.signpost.exception.OAuthNotAuthorizedException

class TaskRetrieveAccessToken: AsyncTask<Void, RuntimeException, LoginResult?>() {

    /**
     * The OAuth provider
     */
    private var commonsHttpOAuthProvider: CommonsHttpOAuthProvider? = null

    /**
     * OAuth Consumer
     */
    private var commonsHttpOAuthConsumer: CommonsHttpOAuthConsumer? = null

    /**
     * variables to hold verifier retrieved from Tumblr
     */
    private var strOAuthVerifier: String? = null

    /**
     * Context of the calling activity
     */
    private var context: Context? = null

    /**
     * A loading Dialog to display to user if passed by developer
     */
    private var loadingDialog: Dialog? = null

    /**
     * Set the context to show progressBars and stuff
     * @param context
     */
    internal fun setContext(context: Context?) {
        this.context = context
    }

    /**
     * Accept Loading dialog passed on by the developer. Null in case no Dialog was passed
     * @param loadingDialog Loading Dialog to display to user
     */
    internal fun setLoadingDialog(loadingDialog: Dialog?) {
        this.loadingDialog = loadingDialog
    }

    /**
     * Set the OAuthConsumer
     * @param OAuthConsumer TheOAuthConsumer to which tokens will be applied
     */
    internal fun setOAuthConsumer(OAuthConsumer: CommonsHttpOAuthConsumer?) {
        this.commonsHttpOAuthConsumer = OAuthConsumer
    }

    /**
     * Set the OAuthProvider
     * @param OAuthProvider The OAuthProvider which makes the request for tokens
     */
    internal fun setOAuthProvider(OAuthProvider: CommonsHttpOAuthProvider?) {
        this.commonsHttpOAuthProvider = OAuthProvider
    }

    /**
     * Set the OAuthVerifier
     * @param OAuthVerifier The OAuthVerifier token which is used as a param to retrieve access tokens
     */
    fun setOAuthVerifier(OAuthVerifier: String) {
        this.strOAuthVerifier = OAuthVerifier
    }

    override fun onPreExecute() {
        super.onPreExecute()
        //If the developer a loading dialog, show that instead of default.
        loadingDialog?.show()
    }

    override fun doInBackground(vararg params: Void?): LoginResult? {
        //Instantiate a new LoginResult object which will store the Consumer key and secret key
        //to be returned to the
        val loginResult: LoginResult = LoginResult()
        try {
            //Queries the service provider for access tokens. The method does not return anything.
            //It stores the OAuthToken & OAuthToken secret in the commonsHttpOAuthConsumer object.
            commonsHttpOAuthProvider?.retrieveAccessToken(commonsHttpOAuthConsumer, strOAuthVerifier)
            //Check if tokens were received. If Yes, save them to SharedPreferences for later use.
            if(!TextUtils.isEmpty(commonsHttpOAuthConsumer?.token)) {
                //Set the consumer key token in the LoginResult object
                loginResult.setStrOAuthToken(commonsHttpOAuthConsumer?.token!!)
                Log.i(TAG, "OAuthToken : " + loginResult.getOAuthToken())
            }

            if(!TextUtils.isEmpty(commonsHttpOAuthConsumer?.tokenSecret)) {
                //Set the Secret consumer key token in the LoginResult object
                loginResult.setStrOAuthTokenSecret(commonsHttpOAuthConsumer?.tokenSecret!!)
                Log.i(TAG, "OAuthSecretToken : " + loginResult.strOAuthTokenSecret)
            }
            //Return the login result with ConsumerKey and ConsumerSecret Key
            return loginResult
        } catch (e: OAuthCommunicationException) {
            e.printStackTrace()
            publishProgress(LoglrLoginException(e.responseBody))
            return null
        } catch (e: OAuthExpectationFailedException) {
            e.printStackTrace()
            publishProgress(LoglrLoginException(e.message!!))
            return null
        } catch (e: OAuthNotAuthorizedException) {
            e.printStackTrace()
            publishProgress(LoglrLoginException(e.responseBody))
            return null
        } catch (e: OAuthMessageSignerException) {
            e.printStackTrace()
            publishProgress(LoglrLoginException(e.message!!))
            return null
        }
    }

    override fun onProgressUpdate(vararg values: RuntimeException?) {
        super.onProgressUpdate(*values)
        if(values.isNotEmpty()) {
            val exception : RuntimeException = values[0]!!
            if(Loglr.instance.exceptionHandler != null)
                Loglr.instance.exceptionHandler?.onLoginFailed(exception)
            else
                finish()
        }
    }

    override fun onPostExecute(result: LoginResult?) {
        super.onPostExecute(result)
        loadingDialog?.dismiss()
        //Check if tokens were retrieved. If yes, Set result as successful and finish activity
        //otherwise, set as failed.
        if(result != null) {
            //Send firebase event for successful login alongwith bundle of method
            Loglr.instance.loginListener?.onLoginSuccessful(result)
        }
        finish()
    }

    /**
     * A method to finish the calling activity
     */
    private fun finish() {
        val loglrActivity: LoglrActivity = context as LoglrActivity
        loglrActivity.finish()
    }

    companion object {
        /**
         * Tag for logging
         */
        private val TAG: String = TaskRetrieveAccessToken.javaClass.simpleName
    }
}