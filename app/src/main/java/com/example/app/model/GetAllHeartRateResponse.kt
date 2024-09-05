package com.example.app.model

// 定义单个心率记录的数据类
data class HeartRate(
    val ID: Int,
    val createdAt: String,
    val tps: Int,
    val updatedAt: String
)

// 定义获取所有心率记录的响应类
class GetAllHeartRateResponse(
    val code: Int,
    val data: List<HeartRate>,
    val message: String
) {
    override fun toString(): String {
        return "GetAllHeartRateResponse(code=$code, data=$data, message='$message')"
    }
}
