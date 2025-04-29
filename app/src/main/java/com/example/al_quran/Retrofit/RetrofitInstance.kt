package com.example.al_quran.Retrofit


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.alquran.cloud/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: QuranApiService by lazy {
        retrofit.create(QuranApiService::class.java)
    }
}
