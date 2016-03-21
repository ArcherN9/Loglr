package com.daksh.loglr.Exceptions;

public class LoglrLoginException extends RuntimeException {

    public LoglrLoginException() {
        super("No LoginListener registered. You need to register a LoginListener using 'setLoginListener();'");
    }

    public LoglrLoginException(String strMessage) {
        super(strMessage);
    }
}
