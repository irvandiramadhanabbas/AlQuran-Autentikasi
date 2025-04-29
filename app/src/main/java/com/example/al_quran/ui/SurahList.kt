package com.example.al_quran.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.al_quran.R
import com.example.al_quran.ViewModel.QuranViewModel
import com.example.al_quran.data.Surah
import com.example.al_quran.data.UserPreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SurahList(
    viewModel: QuranViewModel,
    onSurahClick: (Int) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val surahs = viewModel.surahs.value
    val lastRead = viewModel.lastReadSurah.value
    val user = FirebaseAuth.getInstance().currentUser

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.ebg_islami),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 5.15f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                backgroundColor = Color(0xFF1A1A1A),
                elevation = 6.dp,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile Icon",
                            tint = Color(0xFFFFD700), // gold
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Profil",
                            color = Color(0xFFFFD700),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        user?.photoUrl?.let { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Profile Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        user?.email?.let {
                            Text(
                                text = it,
                                color = Color(0xFFFFD700),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = {
                                val clientId = context.getString(R.string.default_web_client_id)
                                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(clientId)
                                    .requestEmail()
                                    .build()
                                val googleSignInClient = GoogleSignIn.getClient(context, gso)

                                googleSignInClient.signOut().addOnCompleteListener {
                                    FirebaseAuth.getInstance().signOut()
                                    CoroutineScope(Dispatchers.IO).launch {
                                        prefs.clearUser()
                                    }
                                    navController.navigate("login") {
                                        popUpTo("surah_list") { inclusive = true }
                                    }
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            if (lastRead != null) {
                val surah = surahs.find { it.number == lastRead }
                if (surah != null) {
                    LastReadCard(surah = surah) {
                        onSurahClick(surah.number)
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(top = 16.dp)
            ) {
                items(surahs) { surah ->
                    SurahItem(
                        surah = surah,
                        onClick = {
                            viewModel.setLastReadSurah(surah.number)
                            onSurahClick(surah.number)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LastReadCard(surah: Surah, onClick: () -> Unit) {
    Card(
        backgroundColor = Color(0xFFFFC107),
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_masjid),
                contentDescription = "Terakhir dibaca",
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Terakhir Dibaca", color = Color.Black, fontSize = 14.sp)
                Text("${surah.number}. ${surah.englishName}", color = Color.Black, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun SurahItem(surah: Surah, onClick: () -> Unit) {
    Card(
        backgroundColor = Color(0xFF1A1A1A),
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_bismillah),
                contentDescription = "Icon Surah",
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "${surah.number}. ${surah.englishName} (${surah.name})",
                    color = Color.White,
                    fontSize = 18.sp
                )
                Text(
                    text = "Jumlah Ayat: ${surah.numberOfAyahs}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

