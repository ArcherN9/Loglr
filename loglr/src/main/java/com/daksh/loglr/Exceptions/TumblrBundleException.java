package com.daksh.loglr.Exceptions;

public class TumblrBundleException extends RuntimeException {

    public TumblrBundleException() {
        super("Bundle with API Keys missing. Please set Consumer and ConsumerSecret keys to a Bundle and transfer to the activity.");
    }
}
