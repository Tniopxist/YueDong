package com.example.app.http;

import com.example.app.model.InsertExerciseRequest;
import com.example.app.model.InsertExerciseResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RecordService {
    @POST("/record/insert_exercise_record")
    fun insertExerciseRecord(@Body insertExerciseRequest: InsertExerciseRequest?): Call<InsertExerciseResponse>
}
