# Loglr #
### The easiest way to get your user logged in via Tumblr ###

Loglr is an open source library that enables developers to implement 'Login via Tumblr' with as minimum frustration as possible. 

###Dependencies###
```Gradle : compile 'com.daksh:loglr:0.2'```

--- OR ---
```
Maven : 
<dependency>
  <groupId>com.daksh</groupId>
  <artifactId>loglr</artifactId>
  <version>0.2</version>
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
                    
                    //initiate login process and provide a context of Activity
                    .initiate(context); 
```

On overriding the LoginListener, an object of `LoginResult` is received. To extract Token and Secret Token :
```
String strTumblrKey = loginResult.getStrTumblrKey();
String strTumblrSecretKey = loginResult.getStrTumblrSecreyKey();
```

###License###
<one line to give the program's name and a brief idea of what it does.>
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

######Inspired from the works by [jansanz](https://github.com/jansanz) at [TumblrOAuthDemo](https://github.com/jansanz/TumblrOAuthDemo).######
