package daksh.practice.tumblrjumblrimplementation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tumblr.loglr.LoginResult;
import com.tumblr.loglr.Loglr;
import com.tumblr.loglr.LoglrActivity;

/**
 * Created by wits123 on 31/12/15.
 */
public class MainActivity extends AppCompatActivity implements Loglr.LoginListener, Loglr.ExceptionHandler {

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
                    .setConsumerKey("ENTER CONSUMER KEY HERE")
                    .setConsumerSecretKey("ENTER CONSUMER SECRET KEY")
                    .setLoginListener(MainActivity.this)
                    .setExceptionHandler(MainActivity.this)
                    .initiate(MainActivity.this);
        }
    };

    @Override
    public void onLoginSuccessful(LoginResult loginResult) {

    }

    @Override
    public void onLoginFailed(RuntimeException exception) {

    }
}