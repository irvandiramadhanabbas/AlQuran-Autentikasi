package com.example.al_quran.ViewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.al_quran.Retrofit.RetrofitInstance
import com.example.al_quran.data.AyahDisplay
import kotlinx.coroutines.launch

class SurahDetailViewModel : ViewModel() {

    private val _ayahs = mutableStateOf<List<AyahDisplay>>(emptyList())
    val ayahs: State<List<AyahDisplay>> = _ayahs

    fun loadSurahDetail(id: Int) {
        viewModelScope.launch {
            try {
                val arabicResponse = RetrofitInstance.api.getSurahArabic(id)
                val translationResponse = RetrofitInstance.api.getSurahTranslation(id)

                if (arabicResponse.isSuccessful && translationResponse.isSuccessful) {
                    val arabicAyahs = arabicResponse.body()?.data?.ayahs ?: emptyList()
                    val translatedAyahs = translationResponse.body()?.data?.ayahs ?: emptyList()

                    if (arabicAyahs.size == translatedAyahs.size) {
                        val combined = arabicAyahs.zip(translatedAyahs) { ar, tr ->
                            AyahDisplay(
                                numberInSurah = ar.numberInSurah,
                                arabicText = ar.text,
                                translation = tr.text,
                                audioUrl = ar.audio ?: ""
                            )
                        }
                        _ayahs.value = combined
                    } else {
                        println("Jumlah ayat tidak sama antara arab dan terjemahan")
                    }
                } else {
                    println("Gagal memuat data surah: ${arabicResponse.code()} / ${translationResponse.code()}")
                }
            } catch (e: Exception) {
                println("Terjadi kesalahan: ${e.localizedMessage}")
            }
        }
    }
}

