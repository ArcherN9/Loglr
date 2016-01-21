# TumblrJumblrImplementation
A project to demonstrate the implementation of Tumblr OAuth login procedure.

Inspired from the works of [jansanz] at [TumblrOAuthDemo]. The code continues where [TumblrOAuthDemo] left off. Jansanz's examples leaves the developer with only the OAuthVerifier and OAuthToken with no guide on how to continue further. This demo goes ahead and completes the login precedure from start to finish. It can be used 'as is' if you you wish. Just :
* Download Activity file;
* Download Activity Layout;
* Download SignPost libraries from Libs;
* Download Tumblr SharedPreferences Helper method file;
* Enter proper __Consumer Key__ and __Consumer Secret Key__ in Activity Constants;
* Add to build.gradle:

> packagingOptions {
        exclude 'META-INF/NOTICE.txt'  
        exclude 'META-INF/LICENSE.txt'  
        exclude 'META-INF/NOTICE'  
        exclude 'META-INF/LICENSE'  
    }


> compile 'com.tumblr:jumblr:0.0.11'

[jansanz]: https://github.com/jansanz
[TumblrOAuthDemo]: https://github.com/jansanz/TumblrOAuthDemo
