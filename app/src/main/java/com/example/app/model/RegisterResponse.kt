package com.example.app.model

class RegisterResponse(val code: Int, val data: String, val message: String) {
    override fun toString(): String {
        return "RegisterResponse(code=$code, data='$data', message='$message')"
    }
}
