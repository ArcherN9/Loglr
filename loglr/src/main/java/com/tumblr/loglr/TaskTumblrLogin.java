package com.tumblr.loglr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tumblr.loglr.Exceptions.LoglrLoginException;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

/**
 * The AsyncTask performs the following functions
 * 1) Manufactures OAuthConsumer object using Consumer and secret keys
 * 2) Manufactures OAuthProvider object using the URLs mentioned above
 * 3) Makes a network request to retrieve authorization URL. The user is to be navigated
 * to this URL so he may login by entering his/her user credentials.
 */
class TaskTumblrLogin extends AsyncTask<Void, RuntimeException, String> {

    /**
     * Tag for logging
     */
    private final static String TAG = TaskTumblrLogin.class.getSimpleName();

    /**
     * The OAuth provider
     */
    private CommonsHttpOAuthProvider commonsHttpOAuthProvider;

    /**
     * OAuth Consumer
     */
    private CommonsHttpOAuthConsumer commonsHttpOAuthConsumer;

    /**
     * A reference to a progress Dialog
     */
    private ProgressDialog progressDialog;

    /**
     * Android Resources used to access Strings.xml files
     */
    private Resources resources;

    /**
     * Context of the calling activity
     */
    private Context context;

    /**
     * The webview which opens the Tumblr Website
     */
    private WebView webView;

    TaskTumblrLogin() {
        //empty constructor
    }

    /**
     * The Resources set method to give access to resources to the AsyncTask
     * @param resources resources from the Activity
     */
    void setResources(Resources resources) {
        this.resources = resources;
    }

    /**
     * Set the context to show progressBars and stuff
     * @param context
     */
    void setContext(Context context) {
        this.context = context;
    }

    /**
     * A method to pass a reference of the WebView to this AsyncTask class
     * @param view
     */
    void setWebView(View view) {
        this.webView = (WebView) view;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Show a progress Dialog while the request tokens are fetched
        progressDialog = ProgressDialog.show(context, null, resources.getString(R.string.tumblrlogin_loading));
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            //Generate a new oAuthConsumer object
            commonsHttpOAuthConsumer
                    = new CommonsHttpOAuthConsumer(
                    Loglr.getInstance().getConsumerKey(),
                    Loglr.getInstance().getConsumerSecretKey());
            //Generate a new oAuthProvider object
            commonsHttpOAuthProvider
                    = new CommonsHttpOAuthProvider(
                    resources.getString(R.string.tumblr_request),
                    resources.getString(R.string.tumblr_access),
                    resources.getString(R.string.tumblr_auth));
            //Retrieve the URL to which the user must be sent in order to authorize the consumer
            return commonsHttpOAuthProvider.retrieveRequestToken(
                    commonsHttpOAuthConsumer,
                    Loglr.getInstance().getUrlCallBack()
            );
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
            publishProgress(new LoglrLoginException(e.getMessage()));
            return null;
        } catch (OAuthNotAuthorizedException e) {
            e.printStackTrace();
            publishProgress(new LoglrLoginException(e.getResponseBody()));
            return null;
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
            publishProgress(new LoglrLoginException(e.getMessage()));
            return null;
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
            publishProgress(new LoglrLoginException(e.getResponseBody()));
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
    protected void onPostExecute(String strAuthUrl) {
        super.onPostExecute(strAuthUrl);
        //Dismiss progress bar
        progressDialog.dismiss();
        if(!TextUtils.isEmpty(strAuthUrl)) {
            //Enable JS support on web browser - important since TumblrLogin utilises JS components
            //Login page will not show up properly if this is not done
            webView.getSettings().setJavaScriptEnabled(true);
            //Set a web view client to monitor browser interactions
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String strUrl) {
                    //Log Current loading URL
                    Log.i(TAG, strUrl);
                    //Check if the Currently loading URL is that of the call back URL mentioned on top
                    if (strUrl.toLowerCase().contains(Loglr.getInstance().getUrlCallBack().toLowerCase())) {
                        //Parse string URL to conver to URI
                        Uri uri = Uri.parse(strUrl);
                        //instantiate String variables to store OAuth & Verifier tokens
                        String strOAuthToken = "";
                        String strOAuthVerifier = "";
                        //Iterate through Parameters retrieved on the URL
                        for (String strQuery : uri.getQueryParameterNames())
                            switch (strQuery) {
                                case "oauth_token":
                                    //Save OAuth Token
                                    //Note : This is not the login token we require to set on JumblrToken
                                    strOAuthToken = uri.getQueryParameter(strQuery);
                                    break;

                                case "oauth_verifier":
                                    //Save OAuthVerifier
                                    strOAuthVerifier = uri.getQueryParameter(strQuery);
                                    break;
                            }
                        //Execute a new AsyncTask to retrieve access tokens
                        //Performing this is important since communication using OAuthProvider
                        //can only be done in a background thread.
                        TaskRetrieveAccessToken taskRetrieveAccessToken = new TaskRetrieveAccessToken();
                        //Pass OAuthConsumer as an argument
                        taskRetrieveAccessToken.setOAuthConsumer(commonsHttpOAuthConsumer);
                        //Pass OAuthProvider as an argument
                        taskRetrieveAccessToken.setOAuthProvider(commonsHttpOAuthProvider);
                        //Pass context to AsyncTask
                        taskRetrieveAccessToken.setContext(context);
                        //Pass OAuthVerifier as an argument
                        taskRetrieveAccessToken.setOAuthVerifier(strOAuthVerifier);
                        taskRetrieveAccessToken.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        return true;
                    }
                    return super.shouldOverrideUrlLoading(view, strUrl);
                }
            });
            //Load URL
            webView.loadUrl(strAuthUrl);
        } else
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
