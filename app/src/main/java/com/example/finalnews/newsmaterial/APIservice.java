package com.example.finalnews.newsmaterial;

import com.example.finalnews.newsmaterial.model.Root;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIservice {
    String BASE_URL = "https://api.thenewsapi.com/v1/news/";

    @GET("top")
    Call<Root> getTopHeadlines(

            @Query("api_token") String api_token,
            @Query("locale") String language,
            @Query("categories") String category
    );
}
