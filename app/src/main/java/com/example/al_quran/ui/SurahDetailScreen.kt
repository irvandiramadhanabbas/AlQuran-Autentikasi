package com.example.al_quran.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.al_quran.ViewModel.SurahDetailViewModel
import com.example.al_quran.util.AudioPlayer

@Composable
fun SurahDetailScreen(surahId: Int, viewModel: SurahDetailViewModel = viewModel()) {
    LaunchedEffect(surahId) {
        viewModel.loadSurahDetail(surahId)
    }

    val ayahs = viewModel.ayahs.value
    val currentlyPlayingAyahIndex = remember { mutableStateOf(-1) }
    val isPlaying = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    fun convertToArabicNumerals(number: Int): String {
        val arabicNumerals = arrayOf("٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩")
        return number.toString().map { arabicNumerals[(it - '0'.toInt()).code] }.joinToString("")
    }

    fun playAyahAtIndex(index: Int) {
        if (index in ayahs.indices) {
            val ayah = ayahs[index]
            AudioPlayer.play(
                url = ayah.audioUrl,
                onPrepared = {
                    currentlyPlayingAyahIndex.value = index
                    isPlaying.value = true
                },
                onCompletion = {
                    isPlaying.value = false
                    currentlyPlayingAyahIndex.value = -1
                    playAyahAtIndex(index + 1)
                }
            )
        }
    }

    LaunchedEffect(currentlyPlayingAyahIndex.value) {
        if (currentlyPlayingAyahIndex.value >= 0) {
            listState.animateScrollToItem(currentlyPlayingAyahIndex.value)
        }
    }

    if (ayahs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF000000), Color(0xFF212121)))),
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxSize()
            ) {
                itemsIndexed(ayahs) { index, ayah ->
                    val isCurrentPlaying = index == currentlyPlayingAyahIndex.value

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .background(
                                color = if (isCurrentPlaying) Color(0xFF37474F) else Color(0xFF212121),
                                shape = MaterialTheme.shapes.medium
                            )
                            .border(1.dp, Color(0xFFFFC107), MaterialTheme.shapes.medium)
                            .shadow(4.dp, shape = MaterialTheme.shapes.medium)
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = "${convertToArabicNumerals(ayah.numberInSurah)}.",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = MaterialTheme.typography.h6.fontSize,
                                    color = Color(0xFFFFC107)
                                ),
                                modifier = Modifier.padding(end = 12.dp)
                            )

                            Text(
                                text = ayah.arabicText,
                                style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Text(
                            text = "${ayah.translation}",
                            style = MaterialTheme.typography.body1.copy(color = Color(0xFFCFD8DC)),
                            modifier = Modifier.padding(top = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        IconButton(
                            onClick = {
                                if (currentlyPlayingAyahIndex.value == index && isPlaying.value) {
                                    AudioPlayer.pause()
                                    isPlaying.value = false
                                } else if (currentlyPlayingAyahIndex.value == index && !isPlaying.value) {
                                    AudioPlayer.resume()
                                    isPlaying.value = true
                                } else {
                                    AudioPlayer.stop()
                                    playAyahAtIndex(index)
                                }
                            },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Icon(
                                imageVector = if (isCurrentPlaying && isPlaying.value) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "Play/Pause Audio",
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFFFFC107)
                            )
                        }

                        Divider(
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                            color = Color(0xFFCFD8DC)
                        )
                    }
                }
            }
        }
    }
}
