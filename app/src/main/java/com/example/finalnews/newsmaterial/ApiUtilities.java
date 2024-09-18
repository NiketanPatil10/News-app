package com.example.finalnews.newsmaterial;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtilities {

    public static Retrofit retrofit= null;

    public static APIservice getApiInterface(){

        if (retrofit==null){
            retrofit = new Retrofit.Builder().baseUrl(APIservice.BASE_URL).
                    addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit.create(APIservice.class);
    }
}
