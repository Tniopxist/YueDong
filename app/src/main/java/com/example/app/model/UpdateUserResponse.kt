package com.example.app.model

class UpdateUserResponse (
    val code: Int,
    val data: String,
    val message: String) {
    override fun toString(): String {
        return "UpdateUserResponse(code=$code, data='$data', message='$message')"
    }
}