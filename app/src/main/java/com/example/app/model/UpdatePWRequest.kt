package com.example.app.model

class UpdatePWRequest(
    val newPassword: String,
    val oldPassword: String
) {
    override fun toString(): String {
        return "UpdatePWRequest(newPassword=$newPassword, oldPassword=$oldPassword)"
    }
}