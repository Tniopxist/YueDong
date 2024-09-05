package com.example.app.model

class CreateHeartRateRequest(
    val ID: Int,
    val createdAt: String,
    val tps: Int,
    val updatedAt: String
) {
    override fun toString(): String {
        return "CreateHeartRateRequest(ID=$ID, createdAt='$createdAt', tps=$tps, updatedAt='$updatedAt')"
    }
}
