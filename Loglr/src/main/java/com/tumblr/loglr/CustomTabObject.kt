package com.tumblr.loglr

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.customtabs.*
import android.util.Log
import com.tumblr.loglr.Interfaces.AuthorizationCallback

internal class CustomTabObject internal constructor(context: Context) : AuthorizationCallback {

    //Referencing a context received from the primary constructor
    private val context = context
    // Package name for the Chrome channel the client wants to connect to. This
    // depends on the channel name.
    // Stable = com.android.chrome
    // Beta = com.chrome.beta
    // Dev = com.chrome.dev
    private val CUSTOM_TAB_PACKAGE_NAME:String = "com.android.chrome"  // Change when in stable
    //A custom tab client used in conjunction with CustomTabs
    private var customTabClient: CustomTabsClient? = null
    //Create a new session using the customTabClient and associate it with the customtab intent
    private var customTabSession: CustomTabsSession? = null

    init {
        //Bind the CustomTab service with the CustomTab client
        val isBound:Boolean = CustomTabsClient.bindCustomTabsService(context, CUSTOM_TAB_PACKAGE_NAME, object: CustomTabsServiceConnection() {

            override fun onCustomTabsServiceConnected(name: ComponentName?, client: CustomTabsClient?) {
                customTabClient = client
                //Create a new customtab session and monitor changes to the URLs
                customTabSession = customTabClient?.newSession(object: CustomTabsCallback() {

                    override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
                        super.onNavigationEvent(navigationEvent, extras)
                        //Log Current loading URL
                        Log.i(TAG, "User navigation event : " + navigationEvent)
                    }
                })
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                customTabClient = null
            }
        })

        Log.i(CustomTabObject::class.java.simpleName,
                "Warming up CustomTab implementation. bindCustomTabsService returned :" + isBound)
    }

    /**
     * Initializes CustomTabs for the application.
     */
    internal fun begin() {
        //Initialize the TaskTumblrhttps://git.hclets.com/mobility-coe/Android-FirebaseEvents.gitLogin AsyncTask to begin negotiating with the server to log the user in
        val taskTumblrLogin = TaskTumblrLogin()
        //Pass on the Resources folder
        taskTumblrLogin.setResources(context.resources)
        //Pass on the dismiss listener
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
        val customTabIntentBuilder = CustomTabsIntent.Builder(customTabSession)
                .enableUrlBarHiding()
                .setToolbarColor(Loglr.instance.intActionbarColor)

        //Build the final customTabIntent to use to begin login the user
        val customTabIntent = customTabIntentBuilder.build()

        //Load the Custom Tab
        customTabIntent.launchUrl(context, Uri.parse(strUrl))
    }

    companion object {
        //Tag for logging
        val TAG: String = CustomTabObject.javaClass.simpleName
    }
}