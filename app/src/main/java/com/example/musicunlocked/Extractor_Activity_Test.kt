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
                    try {
                        resultText = extractYoutubeInfo(url)
                    } catch (e: Exception) {
                        resultText = "Ошибка: ${e.message}\n${e.stackTraceToString()}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Extract")
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

suspend fun extractYoutubeInfo(url: String): String = withContext(Dispatchers.IO) {
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

    """
    Название: $name
    Длительность: $formattedDuration
    Автор: $author
    Просмотры: ${if (views >= 0) views else "Неизвестно"}
    Лайки: ${if (likes >= 0) likes else "Неизвестно"}
    """.trimIndent()
}
