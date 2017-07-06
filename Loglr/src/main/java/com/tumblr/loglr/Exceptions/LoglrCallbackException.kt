package com.tumblr.loglr.Exceptions

class LoglrCallbackException: RuntimeException("No callback URL registered. Please set a callback URL same as one entered while registering " +
        "aplication with Tumblr using setUrlCallBack()")