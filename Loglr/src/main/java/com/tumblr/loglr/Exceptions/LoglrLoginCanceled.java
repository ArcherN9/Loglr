package com.tumblr.loglr.Exceptions;

/**
 * Created by guesthouser on 6/30/16.
 */
public class LoglrLoginCanceled extends RuntimeException {

    public LoglrLoginCanceled() {
        super("Tumblr login cancelled by user.");
    }

    public LoglrLoginCanceled(String strMessage) {
        super(strMessage);
    }
}