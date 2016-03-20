package daksh.practice.tumblrjumblrimplementation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.tumblr.loglr.Interfaces.ExceptionHandler;
import com.tumblr.loglr.Interfaces.LoginListener;
import com.tumblr.loglr.LoginResult;
import com.tumblr.loglr.Loglr;

public class MainActivity extends AppCompatActivity implements LoginListener, ExceptionHandler {

    /**
     * A tag for logging
     */
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.mainactivity_button).setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Loglr.getInstance()
                    .setConsumerKey("SET CONSUMER KEY HERE")
                    .setConsumerSecretKey("SET CONSUMER SECRET HERE")
                    .setUrlCallBack(getResources().getString(R.string.tumblr_callback_url))
                    .setLoginListener(MainActivity.this)
                    .setExceptionHandler(MainActivity.this)
                    .initiateInActivity(MainActivity.this);
        }
    };

    @Override
    public void onLoginSuccessful(LoginResult loginResult) {
        if(loginResult != null && !TextUtils.isEmpty(loginResult.getOAuthToken()) && !TextUtils.isEmpty(loginResult.getOAuthTokenSecret())) {
            Log.i(TAG, "Tumblr Token : " + loginResult.getOAuthToken());
            Log.i(TAG, "Tumblr Secret Token : " + loginResult.getOAuthTokenSecret());
            Button btnClickMe = (Button) findViewById(R.id.mainactivity_button);
            btnClickMe.setText("Congratulations, Tumblr login succeeded");
            btnClickMe.setEnabled(false);
        }
    }

    @Override
    public void onLoginFailed(RuntimeException exception) {

    }
}