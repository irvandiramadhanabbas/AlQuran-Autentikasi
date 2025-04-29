package com.example.al_quran.data

data class SurahResponse(val data: List<Surah>)

data class Surah(
    val number: Int,
    val name: String,
    val englishName: String,
    val numberOfAyahs: Int,
    val revelationType: String,
    val id: Int,
    val ayahCount: String
)
