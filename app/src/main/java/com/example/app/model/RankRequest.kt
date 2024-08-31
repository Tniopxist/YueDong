package com.example.app.model

class RankRequest (
    val date: String,
    val keyword: String,
    val page: Int,
    val pageSize: Int
) {
    override fun toString(): String {
        return "RankRequest(date='$date', keyword='$keyword', page=$page, pageSize=$pageSize)"
    }
}