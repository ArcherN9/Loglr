[ ![Download](https://api.bintray.com/packages/dakshsrivastava/maven/Loglr/images/download.svg) ](https://bintray.com/dakshsrivastava/maven/Loglr/_latestVersion) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Loglr-green.svg?style=true)](https://android-arsenal.com/details/1/3265)
# Loglr 

![](https://res.cloudinary.com/hashnode/image/upload/w_200,h_200/v1458728299/fuo7n9epkkxyafihrlhz.jpg)

So happy to inform this library has been ported to Kotlin!

### The easiest way to get your user logged in via Tumblr ###

Loglr is library that enables developers to implement 'Login via Tumblr' with as minimum frustration as possible.

Note : The library is in active development. On and off, one may encounter bugs or mistakes. Please report them on the issue tracker.

### Download the demo application from the PlayStore!

<a href='https://play.google.com/store/apps/details?id=daksh.practice.tumblrjumblrimplementation&hl=en&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height="60" width="150"/></a>

### Importing to your project ###
Gradle : 
        
        implementation 'com.daksh:loglr:2.1.4'

### Usage ###

Loglr uses singleton pattern and accepts a minimum of 5 parameters to complete the login process. Each method that accepts a parameter returns the Loglr Instance to avoid boiler plate code.
Retrieve the Loglr instance and set up the Consumer Key and Consumer Secret Key received from Tumblr apps dashboard. You get these when you set up a new app.
 
        Loglr.
            //Set your application consumer key from Tumblr
            .setConsumerKey("ENTER CONSUMER KEY HERE")
         
            //Set your application Secret consumer key from Tumblr
            .setConsumerSecretKey("ENTER CONSUMER SECRET KEY")

Up next, you need to pass an interface that will be called when Login succeeds.
  
            //Implement interface to receive Token and Secret Token
            .setLoginListener(loginListener) 

In case login fails and exceptions are thrown, an exception handler interface needs to be passed so you may ascertain reasons for failure.

            //Interface to receive call backs when things go wrong
            .setExceptionHandler(exceptionHandler)

Amongst the most important parameters, is the URL callback method. Post login, Tumblr redirects the authenticating user to the callback URL set up on Tumblr Apps dashboard. When the URL is received by Loglr, further login procedure is carried out. If no URL is provided, the login shall fail.

            //The URL callback needs to be same as the one entered on the Tumblr Apps Dashboard
            .setUrlCallBack(strUrlCallback)

Optionally, if you prefer having your own loading dialogs replaced with default ones, you may do so by passing the class that extends `Dialog`. This gives you an opportunity to present your users with a consistent experience with the rest of the app elements.
Please note, an object of the custom dialog is manufactured by Loglr by calling the default constructor. Please ensure the class does not require any other parameters to be passed. To view an example, please refer `MainActivity` and `LoadingDialog` classes under app folder.
        
            //Pass the Loading Dialog class
            .setLoadingDialog(LoadingDialog.class)

Loglr supports auto detecting the OTP message received by users with 2 Factor-Authentication enabled. A dialog informing the user is displayed before the permission is requested. If you wish to disable this feature, you may do so by calling the following method and passing `false`. Note : by default, this feature is enabled.

            //Pass a boolean variable that informs loglr if OTP auto detection is to be enabled or not
            .enable2FA(true)
            
To customize the action bar background color and address bar color, you may use :

            //Pass the color resource ID
            .setActionbarColor(R.color.activity_color_actionbar)
            .setTextColor(R.color.activity_color_text)

Finally, start the login procedure.

            //Initiate login in an activity of it's own
            .initiate(context);

When login succeeds, a call back is executed to the LoginListener that was passed with `.setLoginListener(loginListener)` method. An object of `LoginResult` is passed which contains Token and Secret Token  which may be used in conjunction with [Jumlr Library](https://github.com/tumblr/jumblr) to retrieve user information or make requests on user's behalf.

            String strOAuthToken = loginResult.getOAuthToken();
            String strOAuthTokenSecret = loginResult.getOAuthTokenSecret();

To use received tokens with Jumblr :
        
            // Create a new client
            JumblrClient client = new JumblrClient("ENTER CONSUMER KEY HERE", "ENTER CONSUMER SECRET KEY");
            client.setToken(strOAuthToken, strOAuthTokenSecret);

With Jumblr client set, API requests may be made. For more information on basic usage, refer [Tumblr's Jumblr's official guide](https://github.com/tumblr/jumblr).

### Foot notes ###
Permissions listed in manifest :
* `READ_SMS` & `RECEIVE_SMS` : They are used to auto-populate OTP (2 Factor-Authentication) if the user has them enabled on Tumblr.
* `WAKE_LOCK` & `ACCESS_NETWORK_STATE` : Firebase analytics uses these permissions to ensure events are logged properly.

If you wish to remove any permission from your app, add the following line in each of the permissions you wish to remove : `tools:node="remove"`

### Change log ###

##### v2.1.3 #####
* Fixed crashes while reading OTPs;
* Implemented the Kotlin singleton pattern;
* Added more customization options;

##### v2.1.2 #####
* Fixed bugs with Custom dialogs;
* Deleted deprecated files & methods;

##### v2.1.1 #####
* Library ported to Kotlin;
* Removed silly Firebase analytics;
* Removed Fragment implementation of the login;
* Better activity implementation to mimic custom tabs (Tried custom tabs - but due to its limitations, its not possible to use if for OAuth);
* Fixed a bug reported on the bug tracker;

##### v1.2.1 #####
* Developer has option to toggle auto OTP detection for 2FA;
* Better analytics to track user/developer behavior with library;
* Bug fix for corner case when Tumblr OAuth page fails to open;

##### v1.1.1 #####
* Custom Loading Dialogs when tokens are being exchanged and the user is required to wait;
* Auto populate OTP if 2 Factor-Authentication is enabled by user;
* Crash analytics so I may push out new builds if any one is experiencing crashes in their apps due Loglr; 

##### v1.0.0 #####
* Changed Version to v1 post no bug being reported;
* Marked as stable release;

##### v0.3.1 #####
* Support for initiating login process in a DialogFragment;
* Custom URL callback;
* Code clean up;

##### v0.2.2 #####
* Changed method names for better understanding;

##### v0.2.1 #####
* Bug Fixes;

##### v0.2 #####
* First Release;

### Open Source Libraries ###

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

### License ###


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
###### Inspired from the work by [jansanz](https://github.com/jansanz) at [TumblrOAuthDemo](https://github.com/jansanz/TumblrOAuthDemo). ######
