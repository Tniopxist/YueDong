package com.example.app.model

class RankResponse (
    val code: Int,
    val data: data,
    val message: String,
) {
    override fun toString(): String {
        return "RankResponse(code=$code, list='${data.list}', page=${data.page}, pageSize=${data.pageSize}, total=${data.total}, message='$message')"
    }
}

data class data(
    val list: String,
    val page: Int,
    val pageSize: Int,
    val total: Int,
)
