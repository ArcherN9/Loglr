package com.daksh.loglr.Exceptions;

public class TumblrLoginException extends RuntimeException {

    public TumblrLoginException() {
        super("No LoginListener registered. You need to register a LoginListener using 'setLoginListener();'");
    }

    public TumblrLoginException(String strMessage) {
        super(strMessage);
    }
}
