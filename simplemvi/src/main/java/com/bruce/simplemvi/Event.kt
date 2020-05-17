package com.bruce.simplemvi

//This interface is only needed because I don't want to duplicate the standard events everywhere.
interface Event {
    data class ApiErrorEvent(val error: ErrorInfo) : Event

    object InitialView : Event
}