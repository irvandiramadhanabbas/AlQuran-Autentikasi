package com.example.al_quran.Retrofit

import com.example.al_quran.data.SurahDetailResponse
import com.example.al_quran.data.SurahResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApiService {

    @GET("surah")
    suspend fun getSurahList(): Response<SurahResponse>

    @GET("surah/{id}/ar.alafasy")
    suspend fun getSurahArabic(@Path("id") id: Int): Response<SurahDetailResponse>

    @GET("surah/{id}/id.indonesian")
    suspend fun getSurahTranslation(@Path("id") id: Int): Response<SurahDetailResponse>
}
