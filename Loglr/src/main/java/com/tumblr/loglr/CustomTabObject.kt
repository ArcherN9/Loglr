package com.tumblr.loglr

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.support.customtabs.CustomTabsIntent
import com.tumblr.loglr.Interfaces.AuthorizationCallback

internal class CustomTabObject internal constructor(context: Context) : AuthorizationCallback {

    //Referencing a context received from the primary constructor
    private val context = context

    /**
     * Initializes CustomTabs for the application.
     */
    internal fun begin() {
        //Initialize the TaskTumblrLogin AsyncTask to begin negotiating with the server to log the user in
        var taskTumblrLogin = TaskTumblrLogin()
        //Pass on the Resources folder
        taskTumblrLogin.setResources(context.resources)
        //Pass on the dissmiss listener
        //taskTumblrLogin.setDismissListener()
        //Pass the Loading Dialog | Never access loading dialog directly from Loglr class. Go via Utils
        taskTumblrLogin.setLoadingDialog(Utils.getLoadingDialog(context))
        //Get callback for when the Authorization URL has been received successfully
        taskTumblrLogin.setAuthorizationCallback(this@CustomTabObject)

        //Execute the AsyncTask
        taskTumblrLogin.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    /**
     * Interface is executed when TaskTumblrLogin finishes execution and receives the authorization URL
     * to be displayed to the user.
     * @param strUrl The URL to be displayed to the User to log in
     */
    override fun onAuthUrlReceived(strUrl: String) {
        //Instantiate a builder
        val customTabIntentBuilder = CustomTabsIntent.Builder()
                .enableUrlBarHiding()
                .setToolbarColor(Loglr.instance.intActionbarColor)

        //Build the final customTabIntent to use to begin loggin the user in
        var customTabIntent = customTabIntentBuilder.build()

        //Load the Custom Tab
        customTabIntent.launchUrl(context, Uri.parse(strUrl))
    }
}