package com.bruce.simplemvi

interface Event {
    data class ApiErrorEvent(val error: ErrorInfo) : Event

    object InitialView : Event
}