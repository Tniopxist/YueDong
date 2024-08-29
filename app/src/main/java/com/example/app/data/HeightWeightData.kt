package com.example.app.data

import java.time.Duration
import java.time.Instant

data class HeightWeightData(
    val height: Float,
    val weight: Float,
    val timestamp: Instant,
    var duration: Duration = Duration.ZERO
)
