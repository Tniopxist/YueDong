package com.example.app.model

class UpdatePWResponse(
    val code: Int,
    val data: String,
    val message: String) {

    override fun toString(): String {
        return "UpdatePWResponse(code=$code, data='$data', message='$message')"
    }
}