package com.example.musicunlocked

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicunlocked.database.entity.Track
import com.example.musicunlocked.ui.theme.MusicUnlockedTheme
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.schabi.newpipe.extractor.NewPipe

object Session {
    var isLoggedIn = false
    var userId: Int? = null
    var username: String? = null
    var email: String? = null
}

class MainActivity : ComponentActivity() {

    private fun initNewPipe() {
        try {
            NewPipe.init(AppDownloader(OkHttpClient()))
        } catch (_: Exception) {
            // Already initialized or other error
        }
    }

    fun addTrackIfNotExist(name: String, author: String, link: String) {
        lifecycleScope.launch {
            val db = DatabaseProvider.getDb(applicationContext)
            val trackDao = db.TrackDao()
            val existingTrack = trackDao.getTrackByNameAndAuthor(name, author)
            if (existingTrack == null) {
                trackDao.insert(
                    Track(
                        trackName = name,
                        trackAuthor = author,
                        trackLink = link,
                        trackLikes = 0,
                        trackListeners = 0,
                        trackDuration = 0
                    )
                )
                Toast.makeText(this@MainActivity, "Трек добавлен: $name", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Трек уже существует: $name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNewPipe()
        enableEdgeToEdge()
        setContent {
            MusicUnlockedTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        MiniPlayer(onTap = {
                            val intent = Intent(this@MainActivity, TrackActivity::class.java)
                            startActivity(intent)
                        })
                    }
                ) { innerPadding ->
                    MainScreen(
                        onNavigateToProfile = {
                            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                            startActivity(intent)
                        },
                        onNavigateToLibrary = {
                            val intent = Intent(this@MainActivity, UserLibrary::class.java)
                            startActivity(intent)
                        },
                        onNavigateToRegister = {
                            val intent = Intent(this@MainActivity, RegisterScreen::class.java)
                            startActivity(intent)
                        },
                        onNavigateToLogin = {
                            val intent = Intent(this@MainActivity, LoginScreen::class.java)
                            startActivity(intent)
                            finish()
                        },
                        onAddTrack = { name, author, link ->
                            addTrackIfNotExist(name, author, link)
                        },
                        onAddTrackFromYoutube = { url ->
                            lifecycleScope.launch {
                                try {
                                    val result = YoutubeUtils.extractYoutubeInfo(url)
                                    val db = DatabaseProvider.getDb(applicationContext)
                                    val trackDao = db.TrackDao()
                                    val existingTrack = trackDao.getTrackByNameAndAuthor(result.name, result.author)

                                    if (existingTrack == null) {
                                        trackDao.insert(
                                            Track(
                                                trackName = result.name,
                                                trackAuthor = result.author,
                                                trackLink = result.bestAudioUrl ?: "",
                                                trackLikes = result.likeCount.toInt().coerceAtLeast(0),
                                                trackListeners = result.viewCount.toInt().coerceAtLeast(0),
                                                trackDuration = result.durationSeconds * 1000
                                            )
                                        )
                                        Toast.makeText(this@MainActivity, "Трек добавлен!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Обновляем данные
                                        val updatedTrack = existingTrack.copy(
                                            trackLikes = result.likeCount.toInt().coerceAtLeast(0),
                                            trackListeners = result.viewCount.toInt().coerceAtLeast(0),
                                            trackLink = result.bestAudioUrl ?: existingTrack.trackLink,
                                            trackDuration = result.durationSeconds * 1000
                                        )
                                        trackDao.update(updatedTrack)
                                        Toast.makeText(this@MainActivity, "Трек обновлен!", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(this@MainActivity, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        onNavigateToExtractor = {
                            val intent = Intent(this@MainActivity, Extractor_Activity_Test::class.java)
                            startActivity(intent)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onAddTrack: (String, String, String) -> Unit,
    onAddTrackFromYoutube: (String) -> Unit,
    onNavigateToExtractor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playerViewModel: PlayerViewModel = viewModel()
    val context = LocalContext.current
    val db = remember { DatabaseProvider.getDb(context) }
    var allTracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var ytUrl by remember { mutableStateOf("") }

    LaunchedEffect(allTracks) {
        allTracks = db.TrackDao().getAllTracks()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Music Unlocked",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { onNavigateToProfile() }) {
                Text(text = "Профиль")
            }
            Button(onClick = { onNavigateToLibrary() }) {
                Text(text = "Библиотека")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onAddTrack(
                    "SoundHelix8",
                    "Madkid",
                    "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3"
                )
                onAddTrack(
                    "SoundHelix7",
                    "Madkid",
                    "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3"
                )
                onAddTrack(
                    "SoundHelix4",
                    "Madkid",
                    "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"
                )
                onAddTrack(
                    "SoundHelix2",
                    "Madkid",
                    "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
                )
                onAddTrack(
                    "SoundHelix1",
                    "Madkid",
                    "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Добавить тестовые треки.")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onNavigateToExtractor() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Тест экстрактора NewPipe")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = ytUrl,
            onValueChange = { ytUrl = it },
            label = { Text("YouTube URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (ytUrl.isNotBlank()) {
                    onAddTrackFromYoutube(ytUrl)
                    ytUrl = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Добавить новый трек с YouTube")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Все треки в базе данных:", fontSize = 20.sp, modifier = Modifier.align(Alignment.Start))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(allTracks) { index, track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = track.trackName, fontSize = 16.sp)
                        Text(text = track.trackAuthor, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { playerViewModel.setQueue(allTracks, index) }) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
        }

        val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        Session.isLoggedIn = prefs.getBoolean("isLoggedIn", false)
        if (Session.isLoggedIn) {
            Session.userId = prefs.getInt("userId", -1)
            Session.username = prefs.getString("username", null)
            Session.email = prefs.getString("email", null)
        }
        if (!Session.isLoggedIn) {
            onNavigateToLogin()
        }
    }
}
