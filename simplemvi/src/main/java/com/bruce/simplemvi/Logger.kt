package com.bruce.simplemvi

class Logger : ILogger {

    override fun debug(message: String, throwable: Throwable?) {
        println("SimpleMVI: msg $message throwable $throwable")
    }
}