package com.example.app.http

import com.example.app.model.GetUserRequest
import com.example.app.model.GetUserResponse
import com.example.app.model.UpdatePWRequest
import com.example.app.model.UpdatePWResponse
import com.example.app.model.UpdateUserRequest
import com.example.app.model.UpdateUserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {
    @GET("/user/get_user_info")
    fun getUserInfo(): Call<GetUserResponse>

    @POST("/user/update_user_info")
    fun updateUserInfo(@Body UpdateUserRequest: UpdateUserRequest?): Call<UpdateUserResponse>

    @POST("/user/update_user_password")
    fun updatePw(@Body UpdatePWRequest: UpdatePWRequest?): Call<UpdatePWResponse>

}