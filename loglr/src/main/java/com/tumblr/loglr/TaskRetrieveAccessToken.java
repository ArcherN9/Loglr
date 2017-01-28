package com.tumblr.loglr;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.tumblr.loglr.Exceptions.LoglrLoginException;
import com.tumblr.loglr.Interfaces.DismissListener;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

/**
 * The asyncTask utilises the parameters passed and makes a network call to retrieve
 * the access tokens & save them to SharedPreferences.
 */
class TaskRetrieveAccessToken extends AsyncTask<Void, RuntimeException, LoginResult> {

    /**
     * Tag for logging
     */
    private final static String TAG = TaskRetrieveAccessToken.class.getSimpleName();

    /**
     * The OAuth provider
     */
    private CommonsHttpOAuthProvider commonsHttpOAuthProvider;

    /**
     * OAuth Consumer
     */
    private CommonsHttpOAuthConsumer commonsHttpOAuthConsumer;

    /**
     * variables to hold verifier retrieved from Tumblr
     */
    private String strOAuthVerifier;

    /**
     * A reference to a progress Dialog
     */
    private ProgressDialog progressDialog;

    /**
     * Context of the calling activity
     */
    private Context context;

    /**
     * A dismiss listener is to be called in case the AsyncTask's calling context is that
     * of a dialogFragment
     */
    private DismissListener dismissListener;

    /**
     * A loading Dialog to display to user if passed by developer
     */
    private Dialog loadingDialog;

    /**
     * A bundle to store only login related params this bundle is sent alongwith the 'login' event
     */
    private Bundle loginBundle;

    //Constructor
    TaskRetrieveAccessToken() {
    }

    /**
     * A method to pass the dismiss listener to the AsyncTask. Is used exclusively by the
     * dialog Fragment
     * @param dismissListener A reference to the Dismiss listener interface
     */
    void setDismissListener(DismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    /**
     * Accepts a login bundle that will be sent alongwith the login event if and when the login
     * succeeds
     * @param loginBundle
     */
    void setLoginBundle(Bundle loginBundle) {
        this.loginBundle = loginBundle;
    }

    /**
     * Set the context to show progressBars and stuff
     * @param context
     */
    void setContext(Context context) {
        this.context = context;
    }

    /**
     * Accept Loading dialog passed on by the developer. Null in case no Dialog was passed
     * @param loadingDialog Loading Dialog to display to user
     */
    void setLoadingDialog(Dialog loadingDialog) {
        this.loadingDialog = loadingDialog;
    }

    /**
     * Set the OAuthConsumer
     * @param OAuthConsumer TheOAuthConsumer to which tokens will be applied
     */
    public void setOAuthConsumer(CommonsHttpOAuthConsumer OAuthConsumer) {
        this.commonsHttpOAuthConsumer = OAuthConsumer;
    }

    /**
     * Set the OAuthProvider
     * @param OAuthProvider The OAuthProvider which makes the request for tokens
     */
    public void setOAuthProvider(CommonsHttpOAuthProvider OAuthProvider) {
        this.commonsHttpOAuthProvider = OAuthProvider;
    }

    /**
     * Set the OAuthVerifier
     * @param OAuthVerifier The OAuthVerifier token which is used as a param to retrieve access tokens
     */
    public void setOAuthVerifier(String OAuthVerifier) {
        this.strOAuthVerifier = OAuthVerifier;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //If the developer a loading dialog, show that instead of default.
        if(loadingDialog != null)
            loadingDialog.show();
        else
            //Show Progress Dialog while the user waits
            progressDialog = ProgressDialog.show(context, null, "Loading...");
    }

    @Override
    protected LoginResult doInBackground(Void... voids) {
        //Instantiate a new LoginResult object which will store the Consumer key and secret key
        //to be returned to the
        LoginResult loginResult = new LoginResult();
        try {
            //Queries the service provider for access tokens. The method does not return anything.
            //It stores the OAuthToken & OAuthToken secret in the commonsHttpOAuthConsumer object.
            commonsHttpOAuthProvider.retrieveAccessToken(commonsHttpOAuthConsumer, strOAuthVerifier);
            //Check if tokens were received. If Yes, save them to SharedPreferences for later use.
            if(!TextUtils.isEmpty(commonsHttpOAuthConsumer.getToken())) {
                //Set the consumer key token in the LoginResult object
                loginResult.setStrOAuthToken(commonsHttpOAuthConsumer.getToken());
                Log.i(TAG, "OAuthToken : " + loginResult.getOAuthToken());
            }

            if(!TextUtils.isEmpty(commonsHttpOAuthConsumer.getTokenSecret())) {
                //Set the Secret consumer key token in the LoginResult object
                loginResult.setStrOAuthTokenSecret(commonsHttpOAuthConsumer.getTokenSecret());
                Log.i(TAG, "OAuthSecretToken : " + loginResult.getOAuthTokenSecret());
            }
            //Return the login result with ConsumerKey and ConsumerSecret Key
            return loginResult;
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
            publishProgress(new LoglrLoginException(e.getResponseBody()));
            return null;
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
            publishProgress(new LoglrLoginException(e.getMessage()));
            return null;
        } catch (OAuthNotAuthorizedException e) {
            e.printStackTrace();
            publishProgress(new LoglrLoginException(e.getResponseBody()));
            return null;
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
            publishProgress(new LoglrLoginException(e.getMessage()));
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(RuntimeException... values) {
        super.onProgressUpdate(values);
        if(values != null && values.length > 0) {
            RuntimeException exception = values[0];
            if(Loglr.getInstance().getExceptionHandler() != null) {
                if(Loglr.getInstance().getFirebase() != null)
                    Loglr.getInstance().getFirebase().logEvent(context.getString(R.string.FireBase_Event_LoginFailed), loginBundle);
                Loglr.getInstance().getExceptionHandler().onLoginFailed(exception);
            }
            else
                finish();
        }
    }

    @Override
    protected void onPostExecute(LoginResult loginResult) {
        super.onPostExecute(loginResult);
        if(progressDialog != null)
            //Dismiss progress bar
            progressDialog.dismiss();
        else
            loadingDialog.dismiss();
        //Check if tokens were retrieved. If yes, Set result as successful and finish activity
        //otherwise, set as failed.
        if(loginResult != null) {
            //Send firebase event for successful login alongwith bundle of method
            if(Loglr.getInstance().getFirebase() != null)
                Loglr.getInstance().getFirebase().logEvent(FirebaseAnalytics.Event.LOGIN, loginBundle);
            Loglr.getInstance().getLoginListener().onLoginSuccessful(loginResult);
        }
        finish();
    }

    /**
     * A method to finish the calling activity
     */
    private void finish() {
        try {
            LoglrActivity loglrActivity = (LoglrActivity) context;
            loglrActivity.finish();
        } catch (ClassCastException e) {
            //Class cast exception is thrown when the container activity is not
            //LoglrActivity. In such a scenario, it is obvious that Loglr
            //was called using the dialogFragment. In this case, we dismiss the fragment
            //Not printing the stacktrace so it does not go to dev's console
            if(dismissListener != null)
                dismissListener.onDismiss();
        }
    }
}
