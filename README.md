[ ![Download](https://api.bintray.com/packages/dakshsrivastava/maven/Loglr/images/download.svg) ](https://bintray.com/dakshsrivastava/maven/Loglr/_latestVersion) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Loglr-green.svg?style=true)](https://android-arsenal.com/details/1/3265)
# Loglr #
### The easiest way to get your user logged in via Tumblr ###

Loglr is an open source library that enables developers to implement 'Login via Tumblr' with as minimum frustration as possible.

Note : The library is still in development. On and off, one may encounter bugs or mistakes. Please report them on the issue tracker. I'll fix and send out an immediate release.

###Dependencies###
```Gradle : compile 'com.daksh:loglr:1.0.0'```

--- OR ---
```
Maven : 
<dependency>
  <groupId>com.daksh</groupId>
  <artifactId>loglr</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```
###Usage###
```
Loglr.getInstance()
                    //Set your application consumer key from Tumblr
                    .setConsumerKey("ENTER CONSUMER KEY HERE") 
                    
                    //Set your application Secret consumer key from Tumblr
                    .setConsumerSecretKey("ENTER CONSUMER SECRET KEY")
                    
                    //Implement interface to receive Token and Secret Token
                    .setLoginListener(loginListener) 
                    
                    //Interface to receive call backs when things go wrong
                    .setExceptionHandler(exceptionHandler) 
                    
                    //The URL set as a callback on Tumblr
                    //NOTE: Has to be same as the one entered on Tumblr dev console. 
                    //Library will not work otherwise
                    .setUrlCallBack(strUrlCallback)
                    
                    //There are two ways to initiate the login process
                    
                    //First :
                    //initiate login process in an activity
                    .initiateInActivity(context); 
                    
                    //OR
                    
                    //Second :
                    //Initiate the login process in a dialogFragment | The support fragmentManager is a mandatory field
                    .initiateInDialog(getSupportFragmentManager());
```

On overriding the LoginListener, an object of `LoginResult` is received. To extract Token and Secret Token :

    String strOAuthToken = loginResult.getOAuthToken();
    String strOAuthTokenSecret = loginResult.getOAuthTokenSecret();

###Change log###

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
