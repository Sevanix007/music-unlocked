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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamExtractor

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
            // Using a simple check to avoid multiple initializations if activity is recreated
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
                        val result = extractYoutubeInfo(url)
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
                        trackLikes = 0,
                        trackListeners = 0,
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

data class ExtractionResult(
    val name: String,
    val author: String,
    val durationSeconds: Long,
    val bestAudioUrl: String?,
    val fullInfo: String
)

suspend fun extractYoutubeInfo(url: String): ExtractionResult = withContext(Dispatchers.IO) {
    val service = ServiceList.YouTube
    val extractor: StreamExtractor = service.getStreamExtractor(url)
    extractor.fetchPage()

    val name = extractor.name
    val duration = extractor.length
    val author = extractor.uploaderName
    val views = extractor.viewCount
    val likes = try {
        extractor.likeCount
    } catch (_: Exception) {
        -1L
    }

    val formattedDuration = if (duration >= 0) {
        val minutes = duration / 60
        val seconds = duration % 60
        String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
    } else {
        "Неизвестно"
    }

    val audioStreams = extractor.audioStreams
    val bestAudioStream = audioStreams?.maxByOrNull { it.averageBitrate }
    val bestAudioUrl = bestAudioStream?.content

    val audioStreamsInfo = if (audioStreams.isNullOrEmpty()) {
        "Аудиопотоки не найдены"
    } else {
        audioStreams.joinToString("\n\n") { stream ->
            val format = stream.format?.name ?: "Unknown Format"
            val bitrate = if (stream.averageBitrate > 0) "${stream.averageBitrate / 1000} kbps" else "Unknown Bitrate"
            val streamUrl = stream.content
            "Формат: $format\nБитрейт: $bitrate\nURL: $streamUrl"
        }
    }

    val fullInfo = """
    Название: $name
    Длительность: $formattedDuration
    Автор: $author
    Просмотры: ${if (views >= 0) views else "Неизвестно"}
    Лайки: ${if (likes >= 0) likes else "Неизвестно"}
    
    --- Аудиопотоки ---
    $audioStreamsInfo
    """.trimIndent()

    ExtractionResult(
        name = name ?: "Unknown",
        author = author ?: "Unknown",
        durationSeconds = duration,
        bestAudioUrl = bestAudioUrl,
        fullInfo = fullInfo
    )
}
