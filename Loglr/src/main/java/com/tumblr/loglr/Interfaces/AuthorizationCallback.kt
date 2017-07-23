package com.tumblr.loglr.Interfaces

interface AuthorizationCallback {

    /**
     * This method is executed by TaskTumblrLogin executed to query the server to retrieve the
     * login URL to be displayed to the user. When first handshake is completed, TaskTumblrLogin
     * receives the authorization URL and is handed down to the calling Kotlin class. Whoever implements
     * this interface receives the URL.
     */
    fun onAuthUrlReceived(strUrl: String)
}