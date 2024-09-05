package com.example.app.model

data class HealthStatusData(
    val ID: Int,
    val bmi: Float,
    val caloriesBurned: Int,
    val createdAt: String,
    val date: String,
    val distance: Float,
    val exerciseTime: Int,
    val height: Float,
    val stepsCount: Int,
    val updatedAt: String,
    val weight: Float
)

class GetHealthStatusResponse(
    val code: Int,
    val data: HealthStatusData,
    val message: String
) {
    override fun toString(): String {
        return "GetHealthStatusResponse(code=$code, data=$data, message='$message')"
    }
}
