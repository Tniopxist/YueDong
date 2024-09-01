package com.example.app.model

class GetUserResponse(
    val code: Int,
    val data: userdata,
    val message: String
) {
    override fun toString(): String {
        return "GetUserResponse(code=$code,email = ${data.email},gender = ${data.gender},headerImg = ${data.headerImg},nickname = ${data.nickname},phone = ${data.phone}, username = ${data.username},message='$message')"
    }
}

class userdata(
    val email: String,
    val gender: String,
    val headerImg: String,
    val nickname: String,
    val phone: String,
    val username: String
)