package com.tumblr.loglr.Interfaces;

/**
 * Created by dakshsrivastava on 20/03/16.
 */
public interface ExceptionHandler {
    void onLoginFailed(RuntimeException exception);
}
