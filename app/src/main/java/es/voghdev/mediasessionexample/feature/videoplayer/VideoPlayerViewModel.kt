package es.voghdev.mediasessionexample.feature.videoplayer

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class VideoPlayerViewModel : ViewModel() {
    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Initial)
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()
    
    private var player: ExoPlayer? = null
    private var progressUpdateJob: Job? = null
    
    private val videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    
    fun initializePlayer() {
        viewModelScope.launch {
            try {
                _playerState.value = PlayerState.Loading
                
                // Create a new ExoPlayer instance
                val exoPlayer = ExoPlayer.Builder(androidx.core.content.ContextProvider.getApplicationContext())
                    .build()
                    .apply {
                        repeatMode = Player.REPEAT_MODE_OFF
                        playWhenReady = true
                        setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
                        addListener(object : Player.Listener {
                            override fun onIsPlayingChanged(isPlaying: Boolean) {
                                super.onIsPlayingChanged(isPlaying)
                                updateProgressState()
                            }
                            
                            override fun onPlaybackStateChanged(playbackState: Int) {
                                super.onPlaybackStateChanged(playbackState)
                                if (playbackState == Player.STATE_READY) {
                                    updateProgressState()
                                }
                            }
                        })
                        prepare()
                        play()
                    }
                
                player = exoPlayer
                startProgressUpdates()
                _playerState.value = PlayerState.Ready(
                    player = exoPlayer,
                    progress = 0f,
                    currentPosition = 0L,
                    duration = exoPlayer.duration.coerceAtLeast(0)
                )
            } catch (e: Exception) {
                _playerState.value = PlayerState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun releasePlayer() {
        stopProgressUpdates()
        player?.release()
        player = null
        _playerState.value = PlayerState.Initial
    }
    
    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressUpdateJob = viewModelScope.launch {
            while (true) {
                updateProgressState()
                delay(1000) // Update every second
            }
        }
    }
    
    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }
    
    private fun updateProgressState() {
        val currentState = _playerState.value
        if (currentState is PlayerState.Ready) {
            val player = player ?: return
            val duration = player.duration.coerceAtLeast(0)
            val position = player.currentPosition.coerceIn(0, duration)
            val progress = if (duration > 0) position.toFloat() / duration else 0f
            
            _playerState.update { 
                if (it is PlayerState.Ready) {
                    it.copy(
                        progress = progress,
                        currentPosition = position,
                        duration = duration
                    )
                } else {
                    it
                }
            }
        }
    }
    
    fun seekTo(progress: Float) {
        val player = player ?: return
        val duration = player.duration.coerceAtLeast(0)
        val newPosition = (duration * progress).toLong().coerceIn(0, duration)
        player.seekTo(newPosition)
        updateProgressState()
    }
    
    fun play() {
        player?.play()
        updateProgressState()
    }
    
    fun pause() {
        player?.pause()
        updateProgressState()
    }
    
    fun seekForward() {
        player?.let { player ->
            player.seekTo(player.currentPosition + 10_000) // 10 seconds forward
            updateProgressState()
        }
    }
    
    fun seekBackward() {
        player?.let { player ->
            player.seekTo((player.currentPosition - 10_000).coerceAtLeast(0)) // 10 seconds backward
            updateProgressState()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}

sealed class PlayerState {
    object Initial : PlayerState()
    object Loading : PlayerState()
    data class Ready(
        val player: ExoPlayer,
        val progress: Float = 0f,
        val currentPosition: Long = 0L,
        val duration: Long = 0L
    ) : PlayerState()
    data class Error(val message: String) : PlayerState()
}

fun formatDuration(durationMs: Long): String {
    return String.format(
        "%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(durationMs),
        TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
    )
}
