package es.voghdev.mediasessionexample

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VideoPlayerState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isLoading: Boolean = false
)

class VideoPlayerViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _uiState = MutableStateFlow(VideoPlayerState())
    val uiState: StateFlow<VideoPlayerState> = _uiState.asStateFlow()
    
    private var player: ExoPlayer? = null
    
    companion object {
        private const val VIDEO_URL = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        private const val SEEK_INCREMENT_MS = 10000L
    }
    
    init {
        initializePlayer()
    }
    
    private fun initializePlayer() {
        player = ExoPlayer.Builder(getApplication()).build().apply {
            val mediaItem = MediaItem.fromUri(VIDEO_URL)
            setMediaItem(mediaItem)
            prepare()
            
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = playbackState == Player.STATE_BUFFERING
                    )
                }
                
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
                }
            })
        }
        
        startPositionUpdates()
    }
    
    private fun startPositionUpdates() {
        viewModelScope.launch {
            while (true) {
                player?.let { exoPlayer ->
                    _uiState.value = _uiState.value.copy(
                        currentPosition = exoPlayer.currentPosition,
                        duration = if (exoPlayer.duration != C.TIME_UNSET) exoPlayer.duration else 0L
                    )
                }
                delay(1000)
            }
        }
    }
    
    fun playPause() {
        player?.let { exoPlayer ->
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
            } else {
                exoPlayer.play()
            }
        }
    }
    
    fun seekForward() {
        player?.let { exoPlayer ->
            val newPosition = (exoPlayer.currentPosition + SEEK_INCREMENT_MS).coerceAtMost(exoPlayer.duration)
            exoPlayer.seekTo(newPosition)
        }
    }
    
    fun seekBackward() {
        player?.let { exoPlayer ->
            val newPosition = (exoPlayer.currentPosition - SEEK_INCREMENT_MS).coerceAtLeast(0L)
            exoPlayer.seekTo(newPosition)
        }
    }
    
    fun seekTo(positionMs: Long) {
        player?.seekTo(positionMs)
    }
    
    fun getPlayer(): ExoPlayer? = player
    
    override fun onCleared() {
        super.onCleared()
        player?.release()
        player = null
    }
}
