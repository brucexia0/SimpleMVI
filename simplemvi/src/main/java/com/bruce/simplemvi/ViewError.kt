package com.bruce.simplemvi

enum class ErrorAction {
    Toast, Modal, Close
}

data class ErrorInfo(val cause: Int, val action: ErrorAction)