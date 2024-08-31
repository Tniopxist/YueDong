package com.example.app.model

class LoginRequest(
    val captcha: String,
    val captchaId: String,
    val password: String,
    val username: String
) {
    override fun toString(): String {
        return "LoginRequest(captcha='$captcha', captchaId='$captchaId', password='$password', username='$username')"
    }
}
