package com.tumblr.loglr.Interfaces

interface ExceptionHandler {

    fun onLoginFailed(runtimeException: RuntimeException)
}