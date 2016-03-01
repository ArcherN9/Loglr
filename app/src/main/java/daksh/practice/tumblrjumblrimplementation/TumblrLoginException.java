package daksh.practice.tumblrjumblrimplementation;

/**
 * Created by guesthouser on 3/1/16.
 */
public class TumblrLoginException extends Exception {

    public TumblrLoginException() {
        super("No LoginListener registered");
    }
}
