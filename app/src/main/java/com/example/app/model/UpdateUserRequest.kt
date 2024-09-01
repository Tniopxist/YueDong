package com.example.app.model

class UpdateUserRequest(
    val email: String,
    val gender: String,
    val nickname: String,
    val phone: String
) {
    override fun toString(): String {
        return "UpdateUserRequest(email=$email,gender=$gender,nickname=$nickname,phone='$phone')"
    }
}