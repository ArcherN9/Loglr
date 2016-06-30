[ ![Download](https://api.bintray.com/packages/dakshsrivastava/maven/Loglr/images/download.svg) ](https://bintray.com/dakshsrivastava/maven/Loglr/_latestVersion) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Loglr-green.svg?style=true)](https://android-arsenal.com/details/1/3265) [![HitCount](https://hitt.herokuapp.com/dakshsrivastava/Loglr.svg)](https://github.com/dakshsrivastava/Loglr)
# Loglr #
### The easiest way to get your user logged in via Tumblr ###

Loglr is library that enables developers to implement 'Login via Tumblr' with as minimum frustration as possible.

Note : The library is in active development. On and off, one may encounter bugs or mistakes. Please report them on the issue tracker. I'll fix and send out an immediate release.

###Importing to your project###
Gradle : 
        
        compile 'com.daksh:loglr:1.1.1'

Maven : 

        <dependency>
          <groupId>com.daksh</groupId>
          <artifactId>loglr</artifactId>
          <version>1.1.1</version>
          <type>pom</type>
        </dependency>

###Usage###

In your App's AndroidManifest.xml, add the following two permissions. They are used to auto-populate OTP (2 Factor-Authentication) if the user has them enabled on Tumblr.
        
        <uses-permission android:name="android.permission.READ_SMS" />
        <uses-permission android:name="android.permission.RECEIVE_SMS" />

Loglr uses singleton pattern and accepts a minimum of 5 parameters to complete the login process. Each method that accepts a parameter returns the Loglr Instance to avoid boiler plate code.
Retrieve the Loglr instance to start passing parameters to the library. 

        Loglr.getInstance()

Set up the Consumer Key and Consumer Secret Key received from Tumblr apps dashboard. You get these when you set up a new app.
 
        //Set your application consumer key from Tumblr
        .setConsumerKey("ENTER CONSUMER KEY HERE")
         
        //Set your application Secret consumer key from Tumblr
        .setConsumerSecretKey("ENTER CONSUMER SECRET KEY")

Up next, you need to pass an interface that will be called when Login succeeds.
  
        //Implement interface to receive Token and Secret Token
        .setLoginListener(loginListener) 

In case login fails and exceptions are thrown, an exception handler interface need be passed so you may ascertain reasons for failure.

        //Interface to receive call backs when things go wrong
        .setExceptionHandler(exceptionHandler)

Amongst the most important parameters, is the URL callback method. Post login, Tumblr redirects the authenticating user to the callback URL set up on Tumblr Apps dashboard. When the URL is received by Loglr, further login procedure is carried out. If no URL is provided, the login shall fail.

        //The URL callback needs to be same as the one entered on the Tumblr Apps Dashboard
        .setUrlCallBack(strUrlCallback)

Optionally, if you prefer having your own loading dialogs replaced with default ones, you may do so by passing the class that extends `Dialog`. This gives you an opportunity to present your users with a consistent experience with the rest of the app elements.
Please note, an object of the custom dialog is manufactured by Loglr by calling the default constructor. Please ensure the class does not require any other parameters to be passed. To view an example, please refer `MainActivity` and `LoadingDialog` classes under app folder.
        
        //Pass the Loading Dialog class
        .setLoadingDialog(LoadingDialog.class)

Finally, there are two ways to initiate login. The first option is to start it in an activity of it's own. Personally, I believe this is a better approach as the Tumblr user authentication website opens up better. 

        //Initiate login in an activity of it's own
        .initiateInActivity(context);

The second option is to use a Dialog Fragment which hoists on top of your running app. It accepts a support fragment manager as a parameter.
        
        .initiateInDialog(getSupportFragmentManager());

In the event you decide to go ahead with login using a DialogFragment, it is imperative that `onRequestPermissionsResult` method be overridden. Without this, Loglr will incur undesired behaviour. This method will be executed on interaction with the Marshmallow permission dialog. Since the permissions were requested from a DialogFragment, the parent Activity receives the callback. To forward call to the fragment, override the method like so :
        
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            //This line passes callback to the DialogFragment
            Loglr.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

When login succeeds, a call back is executed to the LoginListener that was passed with `.setLoginListener(loginListener)` method. An object of `LoginResult` is passed which contains Token and Secret Token  which may be used in conjunction with [Jumlr Library](https://github.com/tumblr/jumblr) to retrieve user information or make requests on user's behalf.

        String strOAuthToken = loginResult.getOAuthToken();
        String strOAuthTokenSecret = loginResult.getOAuthTokenSecret();

To use received tokens with Jumblr :
        
        // Create a new client
        JumblrClient client = new JumblrClient("ENTER CONSUMER KEY HERE", "ENTER CONSUMER SECRET KEY");
        client.setToken(strOAuthToken, strOAuthTokenSecret);

With Jumblr client set, API requests may be made. For more information on basic usage, refer [Tumblr's Jumblr's official guide](https://github.com/tumblr/jumblr).


###Change log###

#####v1.1.1#####
* Custom Loading Dialogs when tokens are being exchanged and the user is required to wait
* Auto populate OTP if 2 Factor-Authentication is enabled by user
* Crash analytics so I may push out new builds if any one is experiencing crashes in their apps due Loglr 

#####v1.0.0#####
* Changed Version to v1 post no bug being reported
* Marked as stable release

#####v0.3.1#####
* Support for initiating login process in a DialogFragment;
* Custom URL callback;
* Code clean up;

#####v0.2.2#####
* Changed method names for better understanding;

#####v0.2.1#####
* Bug Fixes;

#####v0.2#####
* First Release;

###Open Source Libraries###

* SignPost

```
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```

###License###


    Loglr is an open source library that enables developers to implement 'Login via Tumblr' with as minimum frustration as possible.
    Copyright (C) 2016  Daksh Srivastava

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

### Credits ####
######Inspired from the works by [jansanz](https://github.com/jansanz) at [TumblrOAuthDemo](https://github.com/jansanz/TumblrOAuthDemo).######
