package com.example.app.data

import java.time.Duration
import java.time.Instant

data class BloodPressureData(
    val bp: Int,
    val timestamp: Instant,
    var duration: Duration = Duration.ZERO
)
