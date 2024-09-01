package com.example.app.http

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MyToken(private val context: Context) {
    val BASE_URL = "http://101.42.43.59:8888/"

    val sharedPreferences = context.getSharedPreferences("AppPreferences", MODE_PRIVATE)

    // 动态获取token
    val token: String?
        get() = sharedPreferences.getString("token", "")

    val okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val currentToken = token

        // 创建一个新的请求，将 token 添加到请求头部
        val request = chain.request().newBuilder()
            .addHeader("x-token", "$currentToken")
            .build()

        Log.i("Request Headers", request.headers().toString()) // 打印请求头部信息

        chain.proceed(request)



    }.build()

    // 创建 Retrofit 实例
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

}