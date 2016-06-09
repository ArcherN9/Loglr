package com.tumblr.loglr;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.tumblr.loglr.Exceptions.LoglrAPIException;
import com.tumblr.loglr.Exceptions.LoglrCallbackException;
import com.tumblr.loglr.Exceptions.LoglrLoginException;

import java.lang.reflect.InvocationTargetException;

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

        //Declare a LoadingDialog that will be passed to the AsyncTasks
        Dialog LoadingDialog = null;
        //Test if Loading Dialog was receiver
        if(Loglr.getInstance().getLoadingDialog() != null) {
            try {
                //Extract Loading Dialog class passed by the Activity
                Class<? extends Dialog> classDialog = Loglr.getInstance().getLoadingDialog();
                //get default constructor and create new instance for the Dialog
                LoadingDialog = classDialog.getConstructor(Context.class).newInstance(LoglrActivity.this);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }


        //test if LoginListener was registered
        if(Loglr.getInstance().getLoginListener() != null) {
            if(Loglr.getInstance().getExceptionHandler() == null)
                Log.w(TAG, "Continuing execution without ExceptionHandler. No Exception call backs will be sent. It is recommended to set one.");
            //Initiate an AsyncTask to begin TumblrLogin
            TaskTumblrLogin taskTumblrLogin = new TaskTumblrLogin();
            //Pass context to AsyncTask
            taskTumblrLogin.setContext(LoglrActivity.this);
            //Pass Resources reference
            taskTumblrLogin.setResources(getResources());
            //Pass LoadingDialog as passed on by developer
            taskTumblrLogin.setLoadingDialog(LoadingDialog);
            //Pass reference of WebView
            taskTumblrLogin.setWebView(findViewById(R.id.activity_tumblr_webview));
            //Execute AsyncTask
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