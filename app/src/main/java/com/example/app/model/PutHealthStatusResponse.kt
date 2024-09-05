package com.example.app.model

class PutHealthStatusResponse(
    val code: Int,
    val data: String,
    val message: String
) {
    override fun toString(): String {
        return "CreateBloodPressureResponse(code=$code, data='$data', message='$message')"
    }
}