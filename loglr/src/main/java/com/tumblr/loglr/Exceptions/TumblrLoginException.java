package com.tumblr.loglr.Exceptions;

/**
 * Created by guesthouser on 3/1/16.
 */
public class TumblrLoginException extends RuntimeException {

    public TumblrLoginException() {
        super("No LoginListener registered. You need to register a LoginListener using 'setLoginListener();'");
    }

    public TumblrLoginException(String strMessage) {
        super(strMessage);
    }
}
