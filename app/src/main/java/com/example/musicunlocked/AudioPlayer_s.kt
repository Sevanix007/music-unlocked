package com.example.musicunlocked

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AudioPlayer() {

    val viewModel: PlayerViewModel = viewModel()

    val isPlaying by viewModel.isPlaying
    val currentPosition by viewModel.currentPosition
    val duration by viewModel.duration
    val currentTrackTitle by viewModel.currentTrackTitle

    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(0f) }

    val sliderPosition = if (isDragging) dragPosition else {
        if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = currentTrackTitle,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {

            IconButton(onClick = { viewModel.previous() }) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(
                modifier = Modifier.size(80.dp),
                onClick = {
                    if (isPlaying) {
                        viewModel.pause()
                    } else {
                        viewModel.play()
                    }
                }
            ) {
                Icon(
                    imageVector =
                    if (isPlaying)
                        Icons.Default.Pause
                    else
                        Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
            }

            IconButton(onClick = { viewModel.next() }) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Slider(
            value = sliderPosition.coerceIn(0f, 1f),
            onValueChange = {
                isDragging = true
                dragPosition = it
            },
            onValueChangeFinished = {
                viewModel.seekTo((dragPosition * duration).toLong())
                isDragging = false
            },
            valueRange = 0f..1f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text("Duration: ${formatTime(currentPosition)} / ${formatTime(duration)}")
    }
}

private fun formatTime(ms: Long): String {
    if (ms < 0) return "00:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
