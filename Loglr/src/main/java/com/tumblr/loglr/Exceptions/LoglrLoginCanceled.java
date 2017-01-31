package com.tumblr.loglr.Exceptions;

public class LoglrLoginCanceled extends RuntimeException {

    public LoglrLoginCanceled() {
        super("Tumblr login cancelled by user.");
    }

    public LoglrLoginCanceled(String strMessage) {
        super(strMessage);
    }

    public String getEvent() {
        return "User Cancelled";
    }
}