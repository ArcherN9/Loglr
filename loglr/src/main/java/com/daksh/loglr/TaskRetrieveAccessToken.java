package com.daksh.loglr;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.daksh.loglr.Exceptions.LoglrLoginException;

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

    //Constructor
    TaskRetrieveAccessToken() {
    }

    /**
     * Set the context to show progressBars and stuff
     * @param context
     */
    void setContext(Context context) {
        this.context = context;
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
            if(Loglr.getInstance().getExceptionHandler() != null)
                Loglr.getInstance().getExceptionHandler().onLoginFailed(exception);
            else
                finish();
        }
    }

    @Override
    protected void onPostExecute(LoginResult loginResult) {
        super.onPostExecute(loginResult);
        //Dismiss progress bar
        progressDialog.dismiss();
        //Check if tokens were retrieved. If yes, Set result as successful and finish activity
        //otherwise, set as failed.
        if(loginResult != null)
            Loglr.getInstance().getLoginListener().onLoginSuccessful(loginResult);
        finish();
    }

    /**
     * A method to finish the calling activity
     */
    private void finish() {
        LoglrActivity loglrActivity = (LoglrActivity) context;
        loglrActivity.finish();
    }
}
