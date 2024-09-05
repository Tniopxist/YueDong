package com.example.app.model

import java.time.Instant

// 定义血压记录的数据类
data class BloodPressure(
    val ID: Int,
    val createdAt: String,   // 日期时间字符串
    val diastolic: Int,
    val systolic: Int,
    val updatedAt: String    // 日期时间字符串
)

// 定义响应的数据类
data class GetAllBloodPressureOfUserResponse(
    val code: Int,
    val data: List<BloodPressure>,
    val message: String
)
