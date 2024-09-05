package com.example.app.http

import com.example.app.model.CreateBloodPressureRequest
import com.example.app.model.CreateBloodPressureResponse
import com.example.app.model.CreateHeartRateRequest
import com.example.app.model.CreateHeartRateResponse
import com.example.app.model.GetAllBloodPressureOfUserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BloodPressureService {
    @POST("/blood_pressure/create_blood_pressure")
    fun createBloodPressure(@Body createBloodPressureRequest: CreateBloodPressureRequest): Call<CreateBloodPressureResponse>

    @GET("/blood_pressure/get_all_blood_pressure_of_user")
    fun getAllBloodPressureOfUser(): Call<GetAllBloodPressureOfUserResponse>
}
