package com.example.musicunlocked

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicunlocked.database.entity.Playlist
import com.example.musicunlocked.database.entity.Track
import com.example.musicunlocked.ui.theme.MusicUnlockedTheme

class UserLibrary : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicUnlockedTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Black
                ) { innerPadding ->
                    LibraryScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LibraryScreen(modifier: Modifier = Modifier) {
    var currentView by remember { mutableStateOf("sections") } // "sections", "liked", or "others"
    val context = LocalContext.current
    val db = remember { DatabaseProvider.getDb(context) }
    var likedTracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var otherPlaylists by remember { mutableStateOf<List<Playlist>>(emptyList()) }
    val userId = Session.userId

    LaunchedEffect(currentView) {
        if (userId != null) {
            when (currentView) {
                "liked" -> {
                    val playlist = db.PlaylistDao().getSystemPlaylistByName(userId, "Понравившиеся")
                    if (playlist != null) {
                        likedTracks = db.TrackDao().getTracksByPlaylistId(playlist.playlistId)
                    }
                }
                "others" -> {
                    otherPlaylists = db.PlaylistDao().getPlaylistsByUser(userId).filter { !it.isSystem }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Библиотека",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (currentView == "sections") {
            LibrarySection(title = "Понравившиеся", onClick = { currentView = "liked" })
            Spacer(modifier = Modifier.height(16.dp))
            LibrarySection(title = "Другие плейлисты", onClick = { currentView = "others" })
        } else if (currentView == "liked") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { currentView = "sections" }) {
                    Text(text = "< Назад", color = Color(0xFF00E5FF), fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Понравившиеся",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (likedTracks.isEmpty()) {
                Text(
                    text = "Нет понравившихся треков",
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(likedTracks) { track ->
                        TrackItem(trackName = "${track.trackName} - ${track.trackAuthor}")
                    }
                }
            }
        } else if (currentView == "others") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { currentView = "sections" }) {
                    Text(text = "< Назад", color = Color(0xFF00E5FF), fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Мои плейлисты",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (otherPlaylists.isEmpty()) {
                Text(
                    text = "У вас пока нет других плейлистов",
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(otherPlaylists) { playlist ->
                        LibrarySection(title = playlist.playlistName, onClick = { /* TODO: Show tracks of this playlist */ })
                    }
                }
            }
        }
    }
}

@Composable
fun LibrarySection(title: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        color = Color(0xFF121212),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 4.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 24.dp)
            )
        }
    }
}

@Composable
fun TrackItem(trackName: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF090909),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = trackName,
            color = Color.LightGray,
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}
