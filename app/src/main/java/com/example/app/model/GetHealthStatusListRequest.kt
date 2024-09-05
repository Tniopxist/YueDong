package com.example.app.model

data class GetHealthStatusListRequest(
    val desc: Boolean,
    val endTime: String?,
    val keyword: String?,
    val order: String?,
    val page: Int,
    val pageSize: Int,
    val startTime: String?
) {
    override fun toString(): String {
        return "GetHealthStatusListRequest(desc=$desc, endTime='$endTime', keyword='$keyword', order='$order', page=$page, pageSize=$pageSize, startTime='$startTime')"
    }
}