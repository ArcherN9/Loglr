package com.tumblr.loglr.Exceptions

class LoglrAPIException: RuntimeException("Tumblr API Keys missing. Please set Consumer and ConsumerSecret keys. "
        + "Refer https://github.com/dakshsrivastava/Loglr/blob/master/README.md for details.")