package com.bruce.simplemvi

import android.util.Log

class Logger : ILogger {

    override fun debug(message: String, throwable: Throwable?) {
        Log.d("ImagesDebug", message, throwable)
    }
}