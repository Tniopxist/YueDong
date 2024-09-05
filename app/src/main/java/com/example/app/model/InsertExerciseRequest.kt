package com.example.app.model

class InsertExerciseRequest(
    val ID: Int,
    val avgHeartRate: Int,
    val bloodOxygenLevel: Float,
    val caloriesBurned: Float,
    val createdAt: String,
    val distance: Float,
    val duration: Int,
    val endTime: String,
    val exerciseType: String,
    val highBloodPressure: Float,
    val locationPath: String,
    val lowBloodPressure: Float,
    val startTime: String,
    val stepsCount: Int,
    val updatedAt: String
) {
    override fun toString(): String {
        return "InsertExerciseRequest(ID=$ID, avgHeartRate=$avgHeartRate, bloodOxygenLevel=$bloodOxygenLevel, caloriesBurned=$caloriesBurned, createdAt='$createdAt', distance=$distance,duration=$duration,endTime='$endTime',exerciseType='$exerciseType',highBloodPressure=$highBloodPressure, locationPath='$locationPath', lowBloodPressure=$lowBloodPressure, startTime='$startTime', stepsCount=$stepsCount, updatedAt='$updatedAt')"
    }
}