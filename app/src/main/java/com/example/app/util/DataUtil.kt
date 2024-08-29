package com.example.app.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateUtils {
    // 将 Instant 转换为格式化的日期字符串
    fun formatInstantToDateString(instant: Instant, pattern: String = "MM-dd HH", zoneId: ZoneId = ZoneId.systemDefault()): String {
        val formatter = DateTimeFormatter.ofPattern(pattern).withZone(zoneId)
        return formatter.format(instant)
    }
}