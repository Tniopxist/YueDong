package com.example.app.model

class CreateHeartRateResponse(val code: Int, val data: String, val message: String) {
    override fun toString(): String {
        return "CreateHeartRateResponse(code=$code, data='$data', message='$message')"
    }
}