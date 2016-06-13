package com.tumblr.loglr;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.tumblr.loglr.Exceptions.LoglrAPIException;
import com.tumblr.loglr.Exceptions.LoglrCallbackException;
import com.tumblr.loglr.Exceptions.LoglrLoginException;
import com.tumblr.loglr.Interfaces.DismissListener;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by dakshsrivastava on 20/03/16.
 */
public class LoglrFragment extends DialogFragment implements DismissListener {

    /**
     * A tag for logging
     */
    private static final String TAG = LoglrFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //Remove the fragment title
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.fragment_tumblr_login, null, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        //Set custom height and width of webview
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        getDialog().getWindow().setLayout(
                Double.valueOf(width * 0.95).intValue(),
                Double.valueOf(height * 0.80).intValue()
        );
    }

    @Override
    public void onResume() {
        super.onResume();
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
                LoadingDialog = classDialog.getConstructor(Context.class).newInstance(getActivity());
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
                LoadingDialog = null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                LoadingDialog = null;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                LoadingDialog = null;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                LoadingDialog = null;
            } catch (NullPointerException e) {
                e.printStackTrace();
                LoadingDialog = null;
            }
        }

        //test if LoginListener was registered
        if(Loglr.getInstance().getLoginListener() != null) {
            if(Loglr.getInstance().getExceptionHandler() == null)
                Log.w(TAG, "Continuing execution without ExceptionHandler. No Exception call backs will be sent. It is recommended to set one.");
            //Initiate an AsyncTask to begin Tumblr Login
            TaskTumblrLogin taskTumblrLogin = new TaskTumblrLogin();
            //Pass context to AsyncTask
            taskTumblrLogin.setContext(getActivity());
            //Pass Resources reference
            taskTumblrLogin.setResources(getResources());
            //Pass LoadingDialog as passed on by developer
            taskTumblrLogin.setLoadingDialog(LoadingDialog);
            //Pass dismiss listener
            taskTumblrLogin.setDismissListener(LoglrFragment.this);
            //Pass reference of WebView
            taskTumblrLogin.setWebView(getView().findViewById(R.id.activity_tumblr_webview));
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

    @Override
    public void onDismiss() {
        getDialog().dismiss();
    }
}