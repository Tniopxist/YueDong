package com.example.app.data

import java.time.Duration
import java.time.Instant

data class HeartBeatData(
    val hb: Int,
    val timestamp: Instant,
    var duration: Duration = Duration.ZERO
)

