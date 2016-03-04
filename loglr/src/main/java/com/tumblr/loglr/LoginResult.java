package com.tumblr.loglr;

/**
 * Created by guesthouser on 3/1/16.
 */
public class LoginResult {

    /**
     * The Tumblr Key used to make API calls to Tumblr using Jumble client
     */
    private String strTumblrKey;

    /**
     * The Tumblr Secret Key used to make API calls to Tumblr using Jumble client
     */
    private String strTumblrSecreyKey;

    /**
     * @return Return the TumblrKey
     */
    public String getStrTumblrKey() {
        return strTumblrKey;
    }

    /**
     * Sets the Tumblr Key to the object
     * @param strTumblrKey
     */
    void setStrTumblrKey(String strTumblrKey) {
        this.strTumblrKey = strTumblrKey;
    }

    /**
     * @return Return the Tumblr secret key
     */
    public String getStrTumblrSecreyKey() {
        return strTumblrSecreyKey;
    }

    /**
     * @return Return the TumblrKey
     */
    void setStrTumblrSecreyKey(String strTumblrSecreyKey) {
        this.strTumblrSecreyKey = strTumblrSecreyKey;
    }
}
