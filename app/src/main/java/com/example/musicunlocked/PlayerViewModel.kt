package com.example.musicunlocked

import android.app.Application
import android.content.ComponentName
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicunlocked.database.entity.Playlist
import com.example.musicunlocked.database.entity.Track
import com.example.musicunlocked.database.entity.TrackPlaylist
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _currentPosition = mutableLongStateOf(0L)
    val currentPosition: State<Long> = _currentPosition

    private val _duration = mutableLongStateOf(0L)
    val duration: State<Long> = _duration

    private val _currentTrackTitle = mutableStateOf("")
    val currentTrackTitle: State<String> = _currentTrackTitle

    private val _isLiked = mutableStateOf(false)
    val isLiked: State<Boolean> = _isLiked

    private val _userPlaylists = mutableStateOf<List<Playlist>>(emptyList())
    val userPlaylists: State<List<Playlist>> = _userPlaylists

    private val _playlistsWithCurrentTrack = mutableStateOf<Set<Int>>(emptySet())
    val playlistsWithCurrentTrack: State<Set<Int>> = _playlistsWithCurrentTrack

    private var controller: MediaController? = null

    init {
        val sessionToken = SessionToken(
            application,
            ComponentName(application, MusicService::class.java)
        )

        val future = MediaController.Builder(application, sessionToken).buildAsync()

        future.addListener({
            try {
                controller = future.get().apply {
                    addListener(object : Player.Listener {
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            _isPlaying.value = isPlaying
                        }

                        override fun onPlaybackStateChanged(playbackState: Int) {
                            _duration.longValue = duration.coerceAtLeast(0L)
                            updateDurationInDatabase(currentMediaItem)
                        }

                        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                            _currentTrackTitle.value = mediaItem?.mediaMetadata?.title?.toString() ?: ""
                            _duration.longValue = duration.coerceAtLeast(0L)
                            checkIfLiked(mediaItem)
                            updatePlaylistsInfo(mediaItem)
                        }
                    })
                    _isPlaying.value = isPlaying
                    _duration.longValue = duration.coerceAtLeast(0L)
                    _currentTrackTitle.value = currentMediaItem?.mediaMetadata?.title?.toString() ?: ""
                    checkIfLiked(currentMediaItem)
                    updatePlaylistsInfo(currentMediaItem)
                }
                startPositionTracker()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, MoreExecutors.directExecutor())
    }

    private fun checkIfLiked(mediaItem: MediaItem?) {
        val trackId = mediaItem?.mediaId?.toIntOrNull() ?: return
        val userId = Session.userId ?: return
        
        viewModelScope.launch {
            val db = DatabaseProvider.getDb(getApplication())
            val likedPlaylist = db.PlaylistDao().getSystemPlaylistByName(userId, "Понравившиеся")
            if (likedPlaylist != null) {
                _isLiked.value = db.TrackPlaylistDao().isTrackInPlaylist(likedPlaylist.playlistId, trackId)
            } else {
                _isLiked.value = false
            }
        }
    }

    fun toggleLike() {
        val trackId = controller?.currentMediaItem?.mediaId?.toIntOrNull() ?: return
        val userId = Session.userId ?: return

        viewModelScope.launch {
            val db = DatabaseProvider.getDb(getApplication())
            val playlistDao = db.PlaylistDao()
            val trackPlaylistDao = db.TrackPlaylistDao()

            var likedPlaylist = playlistDao.getSystemPlaylistByName(userId, "Понравившиеся")

            if (likedPlaylist == null) {
                // Создаем Liked плейлист, если его нет
                val newPlaylist = Playlist(
                    playlistName = "Понравившиеся",
                    createdAt = System.currentTimeMillis(),
                    userId = userId,
                    isSystem = true
                )
                playlistDao.insert(newPlaylist)
                likedPlaylist = playlistDao.getSystemPlaylistByName(userId, "Понравившиеся")
            }

            likedPlaylist?.let { playlist ->
                if (_isLiked.value) {
                    trackPlaylistDao.deleteTrackFromPlaylist(playlist.playlistId, trackId)
                    _isLiked.value = false
                } else {
                    trackPlaylistDao.insert(TrackPlaylist(playlistId = playlist.playlistId, trackId = trackId))
                    _isLiked.value = true
                }
            }
        }
    }

    fun updatePlaylistsInfo(mediaItem: MediaItem? = controller?.currentMediaItem) {
        val userId = Session.userId ?: return
        val trackId = mediaItem?.mediaId?.toIntOrNull() ?: return

        viewModelScope.launch {
            val db = DatabaseProvider.getDb(getApplication())
            val playlists = db.PlaylistDao().getPlaylistsByUser(userId).filter { !it.isSystem }
            _userPlaylists.value = playlists

            val inPlaylists = mutableSetOf<Int>()
            for (p in playlists) {
                if (db.TrackPlaylistDao().isTrackInPlaylist(p.playlistId, trackId)) {
                    inPlaylists.add(p.playlistId)
                }
            }
            _playlistsWithCurrentTrack.value = inPlaylists
        }
    }

    fun toggleTrackInPlaylist(playlistId: Int) {
        val trackId = controller?.currentMediaItem?.mediaId?.toIntOrNull() ?: return
        
        viewModelScope.launch {
            val db = DatabaseProvider.getDb(getApplication())
            val dao = db.TrackPlaylistDao()
            
            if (dao.isTrackInPlaylist(playlistId, trackId)) {
                dao.deleteTrackFromPlaylist(playlistId, trackId)
                _playlistsWithCurrentTrack.value = _playlistsWithCurrentTrack.value - playlistId
            } else {
                dao.insert(TrackPlaylist(playlistId = playlistId, trackId = trackId))
                _playlistsWithCurrentTrack.value = _playlistsWithCurrentTrack.value + playlistId
            }
        }
    }

    private fun updateDurationInDatabase(mediaItem: MediaItem?) {
        val trackId = mediaItem?.mediaId?.toIntOrNull() ?: return
        val currentDuration = controller?.duration ?: return
        if (currentDuration > 0) {
            viewModelScope.launch {
                val db = DatabaseProvider.getDb(getApplication())
                val trackDao = db.TrackDao()
                val track = trackDao.getTrackById(trackId)
                if (track != null && track.trackDuration != currentDuration) {
                    trackDao.update(track.copy(trackDuration = currentDuration))
                }
            }
        }
    }

    private fun startPositionTracker() {
        viewModelScope.launch {
            while (true) {
                controller?.let {
                    _currentPosition.longValue = it.currentPosition
                    if (_duration.longValue <= 0) {
                         _duration.longValue = it.duration.coerceAtLeast(0L)
                    }
                }
                delay(500)
            }
        }
    }

    fun play() {
        controller?.play()
    }

    fun pause() {
        controller?.pause()
    }

    fun next() {
        controller?.seekToNext()
    }

    fun previous() {
        controller?.seekToPrevious()
    }

    fun seekTo(position: Long) {
        controller?.seekTo(position)
    }

    fun setQueue(tracks: List<Track>, startIndex: Int = 0) {
        controller?.let { player ->
            val mediaItems = tracks.map { track ->
                MediaItem.Builder()
                    .setMediaId(track.trackId.toString())
                    .setUri(track.trackLink)
                    .setMediaMetadata(
                        androidx.media3.common.MediaMetadata.Builder()
                            .setTitle(track.trackName)
                            .setArtist(track.trackAuthor)
                            .build()
                    )
                    .build()
            }
            player.setMediaItems(mediaItems)
            player.prepare()
            player.seekTo(startIndex, 0L)
            player.play()
        }
    }

    override fun onCleared() {
        controller?.release()
        super.onCleared()
    }
}
