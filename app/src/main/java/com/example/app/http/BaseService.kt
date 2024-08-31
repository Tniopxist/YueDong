package com.example.app.http

import com.example.app.model.RegisterResponse
import com.example.app.model.EmailRequest
import com.example.app.model.EmailResponse
import com.example.app.model.LoginRequest
import com.example.app.model.LoginResponse
import com.example.app.model.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface BaseService {
    @POST("/base/verification_code")
    fun getVerificationCode(@Body emailRequest: EmailRequest?): Call<EmailResponse>

    @POST("/base/register_with_code")
    fun registerWithCode(@Body registerRequest: RegisterRequest?): Call<RegisterResponse>

    @POST("/base/login")
    fun loginWithCode(@Body loginRequest: LoginRequest?): Call<LoginResponse>
}