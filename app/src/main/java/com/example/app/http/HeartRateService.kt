package com.example.app.http

import com.example.app.model.CreateHeartRateRequest
import com.example.app.model.CreateHeartRateResponse
import com.example.app.model.RegisterResponse
import com.example.app.model.EmailRequest
import com.example.app.model.EmailResponse
import com.example.app.model.GetAllHeartRateRequest
import com.example.app.model.GetAllHeartRateResponse
import com.example.app.model.LoginRequest
import com.example.app.model.LoginResponse
import com.example.app.model.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HeartRateService {
    @POST("/heart_rate/create_heart_rate")
    fun createHeartRate(@Body createHeartRateRequest: CreateHeartRateRequest?): Call<CreateHeartRateResponse>

    @GET("/heart_rate/get_all_heart_rate_of_user")
    fun getAllHeartRate(): Call<GetAllHeartRateResponse>
}