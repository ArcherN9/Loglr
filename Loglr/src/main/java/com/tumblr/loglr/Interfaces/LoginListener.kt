package com.tumblr.loglr.Interfaces

import com.tumblr.loglr.LoginResult

interface LoginListener {

    fun onLoginSuccessful(loginResult: LoginResult)
}