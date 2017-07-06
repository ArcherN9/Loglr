package com.tumblr.loglr.Exceptions

class LoglrLoginException(strException: String): RuntimeException("No LoginListener registered. You need to register a LoginListener using 'setLoginListener();'")