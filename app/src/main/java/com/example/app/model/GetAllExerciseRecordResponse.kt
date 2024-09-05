package com.example.app.model

data class ExerciseRecord(
    val ID: Int,
    val avgHeartRate: Float,
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
)

class GetAllExerciseRecordResponse(
    val code: Int,
    val data: List<ExerciseRecord>,
    val message: String
) {
    override fun toString(): String {
        return "GetAllExerciseRecordResponse(code=$code, data=$data, message='$message')"
    }
}
