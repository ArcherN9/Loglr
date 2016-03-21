package com.daksh.loglr;

public class LoginResult {

    /**
     * The Tumblr Key used to make API calls to Tumblr using Jumble client
     */
    private String strOAuthToken;

    /**
     * The Tumblr Secret Key used to make API calls to Tumblr using Jumble client
     */
    private String strOAuthTokenSecret;

    /**
     * @return Return the TumblrKey
     */
    public String getOAuthToken() {
        return strOAuthToken;
    }

    /**
     * Sets the Tumblr Key to the object
     * @param strOAuthToken
     */
    void setStrOAuthToken(String strOAuthToken) {
        this.strOAuthToken = strOAuthToken;
    }

    /**
     * @return Return the Tumblr secret key
     */
    public String getOAuthTokenSecret() {
        return strOAuthTokenSecret;
    }

    /**
     * @return Return the TumblrKey
     */
    void setStrOAuthTokenSecret(String strOAuthTokenSecret) {
        this.strOAuthTokenSecret = strOAuthTokenSecret;
    }
}
