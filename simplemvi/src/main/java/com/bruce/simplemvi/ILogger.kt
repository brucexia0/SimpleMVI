package com.bruce.simplemvi

interface ILogger {
    fun debug(message: String = "", throwable: Throwable? = null)
}