package com.example.al_quran.data

data class SurahDetailResponse(
    val data: SurahDetail
)

data class SurahDetail(
    val name: String,
    val englishName: String,
    val number: Int,
    val ayahs: List<Ayah>
)

data class Ayah(
    val number: Int,
    val text: String,
    val numberInSurah: Int,
    val juz: Int,
    val translation: String = "",
    val audioUrl: String,
    val audio: String?

)
data class AyahDisplay(
    val numberInSurah: Int,
    val arabicText: String,
    val translation: String,
    val audioUrl: String
)
