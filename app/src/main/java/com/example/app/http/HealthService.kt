package com.example.app.http

import com.example.app.model.CreateBloodPressureRequest
import com.example.app.model.CreateBloodPressureResponse
import com.example.app.model.GetAllBloodPressureOfUserResponse
import com.example.app.model.GetHealthStatusListRequest
import com.example.app.model.GetHealthStatusListResponse
import com.example.app.model.GetHealthStatusResponse
import com.example.app.model.PutHealthStatusRequest
import com.example.app.model.PutHealthStatusResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface HealthService {
    @PUT("/health_status/put_health_status")
    fun putHealthStatus(@Body putHealthStatusRequest: PutHealthStatusRequest): Call<PutHealthStatusResponse>

    @GET("/health_status/get_health_status")
    fun getHealthStatus(): Call<GetHealthStatusResponse>

    @POST("/health_status/get_health_status_list")
    fun getHealthStatusList(@Body getHealthStatusListRequest: GetHealthStatusListRequest): Call<GetHealthStatusListResponse>
}