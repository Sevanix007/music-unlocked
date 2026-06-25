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
                        }
                    })
                    _isPlaying.value = isPlaying
                    _duration.longValue = duration.coerceAtLeast(0L)
                    _currentTrackTitle.value = currentMediaItem?.mediaMetadata?.title?.toString() ?: ""
                }
                startPositionTracker()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, MoreExecutors.directExecutor())
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

    override fun onCleared() {
        controller?.release()
        super.onCleared()
    }
}
