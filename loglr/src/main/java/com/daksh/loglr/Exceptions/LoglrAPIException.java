package com.daksh.loglr.Exceptions;

public class LoglrAPIException extends RuntimeException {

    public LoglrAPIException() {
        super("Tumblr API Keys missing. Please set Consumer and ConsumerSecret keys. " +
                "Refer https://github.com/dakshsrivastava/Loglr/blob/master/README.md for details.");
    }
}
