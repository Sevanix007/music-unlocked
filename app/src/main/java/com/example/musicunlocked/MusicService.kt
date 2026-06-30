package com.example.musicunlocked

import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.musicunlocked.database.entity.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MusicService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()

        loadTracksFromDatabase(player)
    }

    private fun loadTracksFromDatabase(player: ExoPlayer) {
        val db = DatabaseProvider.getDb(this)
        serviceScope.launch {
            val tracks = db.TrackDao().getAllTracks()
            val mediaItems = tracks.map { track: Track ->
                MediaItem.Builder()
                    .setMediaId(track.trackId.toString())
                    .setUri(track.trackLink)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(track.trackName)
                            .setArtist(track.trackAuthor)
                            .build()
                    )
                    .build()
            }
            if (mediaItems.isNotEmpty()) {
                player.setMediaItems(mediaItems)
                player.prepare()
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        super.onDestroy()
    }
}
