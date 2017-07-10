package com.tumblr.loglr.Exceptions

class LoglrLoginException(strException: String = "No LoginListener registered. You need to register a LoginListener using 'setLoginListener();'"): RuntimeException("No LoginListener registered. You need to register a LoginListener using 'setLoginListener();'")