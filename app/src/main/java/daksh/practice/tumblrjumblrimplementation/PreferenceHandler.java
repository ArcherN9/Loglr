package daksh.practice.tumblrjumblrimplementation;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by admin on 9/2/2015.
 */
public class PreferenceHandler {

    //SharedPreferences name where data is stored for activity_login elements
    private static final String PREFERENCE_SETTINGS = "PREFERENCE_SETTINGS";
    //The Key in which Twitter Token is stored
    private static final String PREF_TUMBLR_KEY = "PREF_TUMBLR_KEY";
    //The Key in which Twitter Token Secret is stored
    private static final String PREF_TUMBLR_SECRET = "PREF_TUMBLR_SECRET";

    /**
     * Is used to delete all user data
     * @param context the context of the cang aglling activity
     */
    public static void DeletePreferences(Context context) {
        SharedPreferences.Editor sharedPreferences
                = context.getSharedPreferences(PREFERENCE_SETTINGS, Context.MODE_PRIVATE).edit();
        sharedPreferences.clear().apply();
    }

    /**
     * Is used to set the Tumblr Consumer key to the shared pref
     * @param context The context of the calling activity to access the shared pref
     * @param strConsumerKey Tumblr Consumer key
     */
    public static void setTumblrKey(Context context, String strConsumerKey) {
        SharedPreferences.Editor sharedPreferences =
                context.getSharedPreferences(PREFERENCE_SETTINGS, Context.MODE_PRIVATE).edit();
        sharedPreferences.putString(PREF_TUMBLR_KEY, strConsumerKey).apply();
    }

    /**
     * Is used to retrieve the Tumblr consumer key from the shared pref
     * @param context The context of the calling activity to access the shared pref
     */
    public static String getTumblrKey(Context context) {
        SharedPreferences sharedPreferences
                = context.getSharedPreferences(PREFERENCE_SETTINGS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_TUMBLR_KEY, null);
    }

    /**
     * Is used to set the Tumblr Secret Consumer key to the shared pref
     * @param context The context of the calling activity to access the shared pref
     * @param strConsumerKey Tumblr Secret Consumer key
     */
    public static void setTumblrSecret(Context context, String strConsumerKey) {
        SharedPreferences.Editor sharedPreferences =
                context.getSharedPreferences(PREFERENCE_SETTINGS, Context.MODE_PRIVATE).edit();
        sharedPreferences.putString(PREF_TUMBLR_SECRET, strConsumerKey).apply();
    }

    /**
     * Is used to retrieve the Tumblr Secret consumer key from the shared pref
     * @param context The context of the calling activity to access the shared pref
     */
    public static String getTumblrSecret(Context context) {
        SharedPreferences sharedPreferences
                = context.getSharedPreferences(PREFERENCE_SETTINGS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_TUMBLR_SECRET, null);
    }
}