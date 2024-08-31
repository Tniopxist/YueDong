package com.example.app.model

class RegisterRequest(
    val code: String,
    val email: String,
    val password: String,
    val phone: String,
    val username: String
) {
    override fun toString(): String {
        return "RegisterRequest(code='$code', email='$email', password='$password', phone='$phone', username='$username')"
    }
}
