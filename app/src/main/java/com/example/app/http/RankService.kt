package com.example.app.http;

import com.example.app.model.RankRequest
import com.example.app.model.RankResponse
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RankService {
    @POST("/rank/get_rank_list")
    fun getRankList(@Body insertExerciseRequest: RankRequest?): Call<RankResponse>
}
