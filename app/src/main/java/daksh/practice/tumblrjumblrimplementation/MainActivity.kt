package daksh.practice.tumblrjumblrimplementation

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.tumblr.loglr.Interfaces.ExceptionHandler
import com.tumblr.loglr.Interfaces.LoginListener
import com.tumblr.loglr.LoginResult
import com.tumblr.loglr.Loglr
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), LoginListener, ExceptionHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        mainActivity_activity.setOnClickListener(activityClickListener)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private val activityClickListener = View.OnClickListener {
        Loglr.setConsumerKey(getString(R.string.ConsumerKey))
                ?.setConsumerSecretKey(getString(R.string.ConsumerSecretKey))
                ?.setUrlCallBack(resources.getString(R.string.tumblr_callback_url))
                ?.setLoadingDialog(LoadingDialog::class.java)
                ?.setLoginListener(this@MainActivity)
                ?.setExceptionHandler(this@MainActivity)
                ?.enable2FA(true)
                ?.setActionbarColor(R.color.activity_color_actionbar)
                ?.setTextColor(R.color.activity_color_text)
                ?.initiateInActivity(this@MainActivity)
    }

    override fun onLoginSuccessful(loginResult: LoginResult) {
        if (!TextUtils.isEmpty(loginResult.getOAuthToken()) && !TextUtils.isEmpty(loginResult.getOAuthTokenSecret())) {
            Log.i(TAG, "Tumblr Token : " + loginResult.getOAuthToken())
            Log.i(TAG, "Tumblr Secret Token : " + loginResult.getOAuthTokenSecret())
            mainActivity_activity.text = "Congratulations, Tumblr login succeeded"
            mainActivity_activity.isEnabled = false
        }
    }

    override fun onLoginFailed(exception: RuntimeException) {
        Toast.makeText(baseContext, exception.message, Toast.LENGTH_LONG).show()
    }

    companion object {
        /**
         * A tag for logging
         */
        private val TAG = MainActivity::class.java.simpleName
    }
}