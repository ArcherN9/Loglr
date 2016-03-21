package com.daksh.loglr;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.daksh.loglr.Exceptions.LoglrAPIException;
import com.daksh.loglr.Exceptions.LoglrCallbackException;
import com.daksh.loglr.Exceptions.LoglrLoginException;

public class LoglrActivity extends AppCompatActivity {

    /**
     * A tag for logging
     */
    private static final String TAG = LoglrActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tumblr_login);
        //Test if consumer key was received
        if(TextUtils.isEmpty(Loglr.getInstance().getConsumerKey()))
            throw new LoglrAPIException();

        //Test if Secret Key was received
        if(TextUtils.isEmpty(Loglr.getInstance().getConsumerSecretKey()))
            throw new LoglrAPIException();

        //Test if URL Call back was received
        if(TextUtils.isEmpty(Loglr.getInstance().getUrlCallBack()))
            throw new LoglrCallbackException();

        //test if LoginListener was registered
        if(Loglr.getInstance().getLoginListener() != null) {
            if(Loglr.getInstance().getExceptionHandler() == null)
                Log.w(TAG, "Continuing execution without ExceptionHandler. No Exception call backs will be sent. It is recommended to set one.");
            //Initiate an AsyncTask to begin TumblrLogin
            TaskTumblrLogin taskTumblrLogin = new TaskTumblrLogin();
            taskTumblrLogin.setContext(LoglrActivity.this);
            taskTumblrLogin.setResources(getResources());
            taskTumblrLogin.setWebView(findViewById(R.id.activity_tumblr_webview));
            taskTumblrLogin.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            //If Exception handler was registered by the dev, use it to return a call back.
            //Otherwise, just throw the exception and make the application crash
            if (Loglr.getInstance().getExceptionHandler() != null)
                Loglr.getInstance().getExceptionHandler().onLoginFailed(new LoglrLoginException());
            else
                throw new LoglrLoginException();
        }
    }
}