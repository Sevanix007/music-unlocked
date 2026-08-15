package com.example.musicunlocked

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MiniPlayer(onTap: () -> Unit) {
    val viewModel: PlayerViewModel = viewModel()
    val isPlaying by viewModel.isPlaying
    val currentTrackTitle by viewModel.currentTrackTitle
    val currentPosition by viewModel.currentPosition
    val duration by viewModel.duration

    // Если название трека пустое, значит очередь пуста или ничего не выбрано - скрываем плеер
    if (currentTrackTitle.isEmpty()) return

    val progress = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f

    Surface(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 24.dp) // Значительный отступ снизу, чтобы плеер "парил"
            .fillMaxWidth()
            .height(64.dp)
            .clickable { onTap() },
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 8.dp
    ) {
        Column {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder for track image
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = MaterialTheme.shapes.small,
                    color = Color.Gray
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = if (currentTrackTitle.isEmpty()) "Не воспроизводится" else currentTrackTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                    Text(
                        text = "Music Unlocked", // Можно добавить автора, если есть в ViewModel
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = {
                    if (isPlaying) viewModel.pause() else viewModel.play()
                }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause"
                    )
                }

                IconButton(onClick = { viewModel.next() }) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next"
                    )
                }
            }
        }
    }
}

@Composable
fun AudioPlayer() {

    val viewModel: PlayerViewModel = viewModel()

    val isPlaying by viewModel.isPlaying
    val currentPosition by viewModel.currentPosition
    val duration by viewModel.duration
    val currentTrackTitle by viewModel.currentTrackTitle
    val isLiked by viewModel.isLiked
    val userPlaylists by viewModel.userPlaylists
    val playlistsWithTrack by viewModel.playlistsWithCurrentTrack

    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(0f) }
    var showPlaylistDialog by remember { mutableStateOf(false) }

    val sliderPosition = if (isDragging) dragPosition else {
        if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = currentTrackTitle,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {

            // Кнопка лайка
            IconButton(onClick = { viewModel.toggleLike() }) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) Color.Red else LocalContentColor.current,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

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

            IconButton(onClick = { 
                viewModel.updatePlaylistsInfo()
                showPlaylistDialog = true 
            }) {
                Icon(
                    imageVector = Icons.Default.PlaylistAdd,
                    contentDescription = "Add to playlist",
                    modifier = Modifier.size(30.dp),
                    tint = Color(0xFF00E5FF)
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

    if (showPlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showPlaylistDialog = false },
            title = { Text("Добавить в плейлист") },
            text = {
                if (userPlaylists.isEmpty()) {
                    Text("У вас пока нет своих плейлистов. Создайте их в библиотеке.")
                } else {
                    Column {
                        userPlaylists.forEach { playlist ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.toggleTrackInPlaylist(playlist.playlistId) }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = playlistsWithTrack.contains(playlist.playlistId),
                                    onCheckedChange = { viewModel.toggleTrackInPlaylist(playlist.playlistId) }
                                )
                                Text(text = playlist.playlistName, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPlaylistDialog = false }) {
                    Text("Готово")
                }
            }
        )
    }
}

private fun formatTime(ms: Long): String {
    if (ms < 0) return "00:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
