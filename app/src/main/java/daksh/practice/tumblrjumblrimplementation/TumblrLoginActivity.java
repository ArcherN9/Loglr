package daksh.practice.tumblrjumblrimplementation;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

/**
 * Created by wits123 on 31/12/15.
 */
public class TumblrLoginActivity extends AppCompatActivity {

    /**
     * A tag for logging
     */
    private static final String TAG = TumblrLoginActivity.class.getSimpleName();

    /**
     * Tumblr Consumer and Secret keys on which basis the user is logged in
     */
    public static final String TUMBLR_CONSUMER_KEY = "ENTER CONSUMER KEY HERE";
    public static final String TUMBLR_SECRET_KEY = "ENTER CONSUMER SECRET KEY HERE";

    /**
     * Tumblr API URLs
     */
    private static final String TUMBLR_REQUEST = "https://www.tumblr.com/oauth/request_token";
    private static final String TUMBLR_ACCESS = "https://www.tumblr.com/oauth/access_token";
    private static final String TUMBLR_AUTH = "https://www.tumblr.com/oauth/authorize";
    private static final String TUMBLR_CALLBACK = "http://somewebsite.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tumblr_login);
        //Initiate an AsyncTask to begin TumblrLogin
        new TaskTumblrLogin().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * The AsyncTask performs the following functions
     * 1) Manufactures OAuthConsumer object using Consumer and secret keys
     * 2) Manufactures OAuthProvider object using the URLs mentioned above
     * 3) Makes a network request to retrieve authorization URL. The user is to be navigated
     * to this URL so he may login by entering his/her user credentials.
     */
    private class TaskTumblrLogin extends AsyncTask<Void, Void, String> {

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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Show a progress Dialog while the request tokens are fetched
            progressDialog = ProgressDialog.show(TumblrLoginActivity.this, null, "Loading...");
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                //Generate a new oAuthConsumer object
                commonsHttpOAuthConsumer
                        = new CommonsHttpOAuthConsumer(TUMBLR_CONSUMER_KEY, TUMBLR_SECRET_KEY);
                //Generate a new oAuthProvider object
                commonsHttpOAuthProvider
                        = new CommonsHttpOAuthProvider(TUMBLR_REQUEST, TUMBLR_ACCESS, TUMBLR_AUTH);
                //Retrieve the URL to which the user must be sent in order to authorize the consumer
                return commonsHttpOAuthProvider.retrieveRequestToken(commonsHttpOAuthConsumer, TUMBLR_CALLBACK);
            } catch (OAuthMessageSignerException e) {
                e.printStackTrace();
                return null;
            } catch (OAuthNotAuthorizedException e) {
                e.printStackTrace();
                return null;
            } catch (OAuthExpectationFailedException e) {
                e.printStackTrace();
                return null;
            } catch (OAuthCommunicationException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String strAuthUrl) {
            super.onPostExecute(strAuthUrl);
            //Dismiss progress bar
            progressDialog.dismiss();
            if(!TextUtils.isEmpty(strAuthUrl)) {
                //instantiate web view to authorize user account
                final WebView webView = (WebView) findViewById(R.id.activity_tumblr_webview);
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
                        if (strUrl.contains(TUMBLR_CALLBACK.toLowerCase())) {
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
            } else {
                //Authorization URL was not received, finish the activity, set 'Failed' in activity
                //result
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    /**
     * The asyncTask utilises the parameters passed and makes a network call to retrieve
     * the access tokens & save them to SharedPreferences.
     */
    private class TaskRetrieveAccessToken extends AsyncTask<Void, Void, Boolean> {

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

        //Constructor
        public TaskRetrieveAccessToken() {
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
            progressDialog = ProgressDialog.show(TumblrLoginActivity.this, null, "Loading...");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                //Queries the service provider for access tokens. The method does not return anything.
                //It stores the OAuthToken & OAuthToken secret in the commonsHttpOAuthConsumer object.
                commonsHttpOAuthProvider.retrieveAccessToken(commonsHttpOAuthConsumer, strOAuthVerifier);
                //Check if tokens were received. If Yes, save them to SharedPreferences for later use.
                if(!TextUtils.isEmpty(commonsHttpOAuthConsumer.getToken())) {
                    PreferenceHandler.setTumblrKey(getBaseContext(), commonsHttpOAuthConsumer.getToken());
                    Log.i(TAG, "OAuthToken : " + PreferenceHandler.getTumblrKey(getBaseContext()));
                } else
                    return false;

                if(!TextUtils.isEmpty(commonsHttpOAuthConsumer.getTokenSecret())) {
                    PreferenceHandler.setTumblrSecret(getBaseContext(), commonsHttpOAuthConsumer.getTokenSecret());
                    Log.i(TAG, "OAuthSecretToken : " + PreferenceHandler.getTumblrSecret(getBaseContext()));
                } else
                    return false;

                return true;
            } catch (OAuthCommunicationException e) {
                e.printStackTrace();
                return false;
            } catch (OAuthExpectationFailedException e) {
                e.printStackTrace();
                return false;
            } catch (OAuthNotAuthorizedException e) {
                e.printStackTrace();
                return false;
            } catch (OAuthMessageSignerException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            //Dismiss progress bar
            progressDialog.dismiss();
            //Check if tokens were retrieved. If yes, Set result as successful and finish activity
            //otherwise, set as failed.
            if(aBoolean)
                setResult(RESULT_OK);
            else {
                setResult(RESULT_CANCELED);
                //Delete shared preferences if tokens were not received.
                PreferenceHandler.DeletePreferences(getBaseContext());
            }
            finish();
        }
    }
}