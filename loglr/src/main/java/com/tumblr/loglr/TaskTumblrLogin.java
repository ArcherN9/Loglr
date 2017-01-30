package com.tumblr.loglr;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tumblr.loglr.Exceptions.LoglrLoginException;
import com.tumblr.loglr.Interfaces.DismissListener;
import com.tumblr.loglr.Interfaces.OTPReceiptListener;

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
class TaskTumblrLogin extends AsyncTask<Void, RuntimeException, String> implements
        OTPReceiptListener {

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
     * A loading Dialog to display to user if passed by developer
     */
    private Dialog loadingDialog;

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

    /**
     * The OTP received from Tumblr
     */
    private String strOTP;

    /**
     * A dismiss listener is to be called in case the AsyncTask's calling context is that
     * of a dialogFragment
     */
    private DismissListener dismissListener;

    /**
     * A bundle to store only login related params this bundle is sent alongwith the 'login' event
     */
    private Bundle loginBundle = new Bundle();

    TaskTumblrLogin() {
        //empty constructor
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
     * Accept Loading dialog passed on by the developer. Null in case no Dialog was passed
     * @param loadingDialog Loading Dialog to display to user
     */
    void setLoadingDialog(Dialog loadingDialog) {
        this.loadingDialog = loadingDialog;
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

        //If the developer a loading dialog, show that instead of default.
        if(loadingDialog != null) {
            loadingDialog.show();
            if(Loglr.getInstance().getFirebase() != null)
                Loglr.getInstance().getFirebase().logEvent(context.getString(R.string.FireBase_Event_CustomDialogSet), null);
        } else
            //Show Progress Dialog while the user waits
            progressDialog = ProgressDialog.show(context, null, "Loading...");
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
            publishProgress(new LoglrLoginException(e.getMessage()));
            return null;
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
            publishProgress(new LoglrLoginException(e.getMessage()));
            return null;
        } catch (OAuthCommunicationException e) {
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
                loginBundle.putString(context.getString(R.string.FireBase_Param_Reason), exception.getMessage());
                if(Loglr.getInstance().getFirebase() != null)
                    Loglr.getInstance().getFirebase().logEvent(context.getString(R.string.FireBase_Event_LoginFailed), loginBundle);
                Loglr.getInstance().getExceptionHandler().onLoginFailed(exception);
            }
            else
                finish();
        }
    }

    @Override
    protected void onPostExecute(String strAuthUrl) {
        super.onPostExecute(strAuthUrl);
        //Dismiss progress bar
        if(progressDialog != null)
            progressDialog.dismiss();
        else
            loadingDialog.dismiss();

        if(!TextUtils.isEmpty(strAuthUrl)) {
            //Enable JS support on web browser - important since TumblrLogin utilises JS components
            //Login page will not show up properly if this is not done
            webView.getSettings().setJavaScriptEnabled(true);
            //Set a web view client to monitor browser interactions
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if(!TextUtils.isEmpty(url)
                            && url.equalsIgnoreCase(context.getResources().getString(R.string.tumblr_auth_otp_redirect))
                            && !TextUtils.isEmpty(strOTP)
                            && !strOTP.equalsIgnoreCase(String.valueOf(0))) {
                        webView.loadUrl("javascript:document.getElementById(\"tfa_response_field\").value= " + strOTP + " ;");

                        Bundle bundle = new Bundle();
                        bundle.putBoolean(context.getString(R.string.FireBase_Param_AutoFill), true);
                        if(Loglr.getInstance().getFirebase() != null)
                            Loglr.getInstance().getFirebase().logEvent(context.getString(R.string.FireBase_Event_2FA), bundle);
                    }
                }

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
                        //Pass Loading Dialog
                        taskRetrieveAccessToken.setLoadingDialog(loadingDialog);
                        //Pass OAuthVerifier as an argument
                        taskRetrieveAccessToken.setOAuthVerifier(strOAuthVerifier);
                        //Set the Dismiss listener
                        taskRetrieveAccessToken.setDismissListener(dismissListener);
                        //Pass the login bundle as well | Will be used when the login succeeds in the end
                        taskRetrieveAccessToken.setLoginBundle(loginBundle);
                        //Execute the AsyncTask on a different thread;
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

    @Override
    public void onReceived(WebView webview, String strOTP) {
        Log.i(TAG, "OTP Received to populate WebView : " + strOTP);
        this.strOTP = strOTP;
        if(webView != null)
            webview.loadUrl("javascript:document.getElementById(\"tfa_response_field\").value=" + strOTP + ";");
    }
}