package com.example.app.model

data class LoginResponse(
    val code: Int,
    val data: LoginData,
    val message: String,
    val msg: String
) {
    override fun toString(): String {
        return "LoginResponse(code=$code, data=$data, message='$message', msg='$msg')"
    }
}

data class LoginData(
    val expiresAt: Long,
    val token: String,
    val user: User
)

data class User(
    val ID: Int,
    val authorities: List<Authority>,
    val authority: Authority,
    val authorityId: Int,
    val createdAt: String,
    val email: String,
    val enable: Int,
    val gender: String,
    val headerImg: String,
    val nickName: String,
    val phone: String,
    val updatedAt: String,
    val userName: String,
    val uuid: String
)

data class Authority(
    val authorityId: Int,
    val authorityName: String,
    val children: List<String>,
    val createdAt: String,
    val dataAuthorityId: List<String>,
    val defaultRouter: String,
    val deletedAt: String,
    val parentId: Int,
    val updatedAt: String
)
