package com.example.app.data

data class UserDistanceRankData(var userDistanceData:List<UserDistanceData>)

data class UserDistanceData(
    var userId:Int,
    var headerImg:String,
    val nickName:String,
    val distance:Float,
    var rank: Int)
