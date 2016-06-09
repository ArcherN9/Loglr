package daksh.practice.tumblrjumblrimplementation;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by guesthouser on 6/9/16.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class LoginTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void login() {
        Espresso.onView(ViewMatchers.withId(R.id.mainactivity_button))
                .perform(ViewActions.click());
    }
}
