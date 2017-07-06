package com.tumblr.loglr;

import android.annotation.SuppressLint;
import android.app.Dialog;
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

    TaskTumblrLogin() {
        //empty constructor
    }
}