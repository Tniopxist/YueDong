package com.example.app.data

import java.time.Duration
import java.time.Instant

data class ExerciseRecordData(
    val duration: Int,
    val distance: Float,
    val avgSpeed: Double,
    val caloriesBurned: Float,
    val startTime: String
)