package com.example.app.model

import com.example.app.data.UserDistanceData
import com.example.app.data.UserDistanceRankData

class RankResponse (
    val code: Int,
    val data: data,
    val message: String,
) {
    override fun toString(): String {
        return "RankResponse(code=$code, page=${data.page}, pageSize=${data.pageSize}, total=${data.total}, message='$message')"
    }
}

data class data(
    val list: List<UserDistanceData>,
    val page: Int,
    val pageSize: Int,
    val total: Int,
)
