package com.tumblr.loglr.Exceptions;

public class LoglrCallbackException extends RuntimeException {

    public LoglrCallbackException() {
        super("No callback URL registered. Please set a callback URL same as one entered while registering " +
                "aplication with Tumblr using setUrlCallBack()");
    }
}
