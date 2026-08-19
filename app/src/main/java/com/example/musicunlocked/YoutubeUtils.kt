package com.example.musicunlocked

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamExtractor


// вот этот класс оно возвращает для музыки. Взято из теста
// Надо оптимизировать, позже придумаю как. есть идеи добавить скачивание.
data class ExtractionResult(
    val name: String,
    val author: String,
    val durationSeconds: Long,
    val viewCount: Long,
    val likeCount: Long,
    val bestAudioUrl: String?,
    val fullInfo: String
)

object YoutubeUtils {
    suspend fun extractYoutubeInfo(url: String): ExtractionResult = withContext(Dispatchers.IO) {
        val service = ServiceList.YouTube
        val extractor: StreamExtractor = service.getStreamExtractor(url)
        extractor.fetchPage()

        val name = extractor.name ?: "Unknown"
        val duration = extractor.length
        val author = extractor.uploaderName ?: "Unknown"
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
            name = name,
            author = author,
            durationSeconds = duration,
            viewCount = views,
            likeCount = likes,
            bestAudioUrl = bestAudioUrl,
            fullInfo = fullInfo
        )
    }
}
