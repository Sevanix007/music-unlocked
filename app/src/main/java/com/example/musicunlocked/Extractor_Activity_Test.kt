package com.example.musicunlocked

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicunlocked.database.entity.Track
import com.example.musicunlocked.ui.theme.MusicUnlockedTheme
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.schabi.newpipe.extractor.NewPipe

class Extractor_Activity_Test : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Ensure session is active for MusicService connection during tests
        if (!Session.isLoggedIn) {
            Session.isLoggedIn = true
            Session.username = "TestUser"
        }

        try {
            // NewPipe.init should only be called once.
            if (!isNewPipeInitialized) {
                NewPipe.init(AppDownloader(OkHttpClient()))
                isNewPipeInitialized = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            MusicUnlockedTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ExtractorScreen()
                }
            }
        }
    }
    
    companion object {
        private var isNewPipeInitialized = false
    }
}

@Composable
fun ExtractorScreen() {
    var url by remember { mutableStateOf("https://www.youtube.com/watch?v=dQw4w9WgXcQ") }
    var resultText by remember { mutableStateOf("Результаты появятся здесь") }
    var extractionResult by remember { mutableStateOf<ExtractionResult?>(null) }
    
    val playerViewModel: PlayerViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "NewPipe Extractor Test",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("YouTube URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                scope.launch {
                    resultText = "Extracting..."
                    extractionResult = null
                    try {
                        val result = YoutubeUtils.extractYoutubeInfo(url)
                        extractionResult = result
                        resultText = result.fullInfo
                    } catch (e: Exception) {
                        resultText = "Ошибка: ${e.message}\n${e.stackTraceToString()}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Extract")
        }

        extractionResult?.bestAudioUrl?.let { audioUrl ->
            Button(
                onClick = {
                    val track = Track(
                        trackName = extractionResult?.name ?: "Unknown",
                        trackAuthor = extractionResult?.author ?: "Unknown",
                        trackLink = audioUrl,
                        trackLikes = extractionResult?.likeCount?.toInt() ?: 0,
                        trackListeners = extractionResult?.viewCount?.toInt() ?: 0,
                        trackDuration = extractionResult?.durationSeconds?.times(1000) ?: 0L
                    )
                    playerViewModel.setQueue(listOf(track))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("Play Audio")
            }
        }

        if (playerViewModel.currentTrackTitle.value.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        text = "Сейчас играет: ${playerViewModel.currentTrackTitle.value}",
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = {
                        if (playerViewModel.isPlaying.value) playerViewModel.pause() else playerViewModel.play()
                    }) {
                        Text(if (playerViewModel.isPlaying.value) "Pause" else "Play")
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = resultText,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
