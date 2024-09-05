package com.example.app.model

class PutHealthStatusRequest(
    val ID: Int,
    val bmi: Float,
    val caloriesBurned: Float,
    val createdAt: String,
    val date: String,
    val distance: Float,
    val exerciseTime: Int,
    val height: Float,
    val stepsCount: Int,
    val updatedAt: String,
    val weight: Float
) {
    override fun toString(): String {
        return "PutHealthStatusRequest(ID=$ID, bmi=$bmi, caloriesBurned=$caloriesBurned, createdAt='$createdAt', date='$date', distance=$distance, exerciseTime=$exerciseTime, height=$height, stepsCount=$stepsCount, updatedAt='$updatedAt', weight=$weight)"
    }
}
