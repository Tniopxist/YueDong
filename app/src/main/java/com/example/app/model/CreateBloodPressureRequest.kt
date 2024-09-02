package com.example.app.model

class CreateBloodPressureRequest(
    val ID: Int,
    val createdAt: String,
    val diastolic: Int,
    val systolic: Int,
    val updatedAt: String
) {
    override fun toString(): String {
        return "CreateBloodPressureRequest(ID=$ID, createdAt='$createdAt', diastolic=$diastolic, systolic=$systolic, updatedAt='$updatedAt')"
    }
}
