package com.example.app.model

import java.nio.charset.StandardCharsets

class EmailResponse(val code: Int, val data: String, val message: String) {
    override fun toString(): String {
        return "EmailResponse(code=$code, data='$data', message='$message')"
    }
}