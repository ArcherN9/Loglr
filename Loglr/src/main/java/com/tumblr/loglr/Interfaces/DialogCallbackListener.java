package com.tumblr.loglr.Interfaces;

public interface DialogCallbackListener {

    void onPermissionDenied();

    void onPermissionGranted();

    void onNegativePressed();
}
