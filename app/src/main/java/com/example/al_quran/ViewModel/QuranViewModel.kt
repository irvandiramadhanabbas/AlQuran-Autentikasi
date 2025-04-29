package com.example.al_quran.ViewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.al_quran.Retrofit.RetrofitInstance
import com.example.al_quran.data.Surah
import kotlinx.coroutines.launch

class QuranViewModel : ViewModel() {
    private val _surahs = mutableStateOf<List<Surah>>(emptyList())
    val surahs: State<List<Surah>> = _surahs

    private val _lastReadSurah = mutableStateOf<Int?>(null)
    val lastReadSurah: State<Int?> = _lastReadSurah

    init {
        getSurahs()
    }

    private fun getSurahs() {
        viewModelScope.launch {
            val response = RetrofitInstance.api.getSurahList()
            if (response.isSuccessful) {
                _surahs.value = response.body()?.data ?: emptyList()
            }
        }
    }

    fun setLastReadSurah(surahNumber: Int) {
        _lastReadSurah.value = surahNumber
    }
}
