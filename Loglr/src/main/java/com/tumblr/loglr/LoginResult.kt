package com.tumblr.loglr

class LoginResult internal constructor() {

    /**
     * The Tumblr Key used to make API calls to Tumblr using Jumble client
     */
    internal var strOAuthToken: String = ""

    /**
     * The Tumblr Secret Key used to make API calls to Tumblr using Jumble client
     */
    internal var strOAuthTokenSecret: String = ""

    /**
     * @return Return the TumblrKey
     */
    fun getOAuthToken(): String = strOAuthToken

    /**
     * Sets the Tumblr Key to the object
     * @param strOAuthToken
     */
    fun setStrOAuthToken(oAuthToken: String) {
        strOAuthToken = oAuthToken
    }

    /**
     * @return Return the Tumblr secret key
     */
    fun getOAuthTokenSecret(): String = strOAuthTokenSecret

    /**
     * @return
     */
    fun setStrOAuthTokenSecret(OAuthTokenSecret: String) {
        strOAuthTokenSecret = OAuthTokenSecret
    }
}