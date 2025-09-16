package es.voghdev.mediasessionexample.feature.videoplayer

import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    viewModel: VideoPlayerViewModel,
    onBack: () -> Unit
) {
    val playerState by viewModel.playerState
    val context = LocalContext.current
    var showControls by remember { mutableStateOf(true) }
    
    // Toggle controls visibility
    val toggleControls = {
        showControls = !showControls
    }

    // Initialize the player when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.initializePlayer()
    }

    // Clean up the player when the screen is removed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.releasePlayer()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Video player content
        when (playerState) {
            is PlayerState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading video...", color = Color.White)
                }
            }
            is PlayerState.Ready -> {
                val player = (playerState as PlayerState.Ready).player
                
                // Player view
                AndroidView(
                    factory = { context ->
                        PlayerView(context).apply {
                            this.player = player
                            useController = false
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setOnClickListener { toggleControls() }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
                // Custom controls overlay
                AnimatedVisibility(
                    visible = showControls,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    ) {
                        // Top bar with back button
                        TopAppBar(
                            title = { Text("Video Player") },
                            navigationIcon = {
                                IconButton(
                                    onClick = onBack,
                                    modifier = Modifier.background(
                                        color = Color.Black.copy(alpha = 0.5f),
                                        shape = MaterialTheme.shapes.small
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                    )
                                }
                            },
                            colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                titleContentColor = Color.White,
                                actionIconContentColor = Color.White
                            )
                        )
                        
                        // Bottom controls
                        if (playerState is PlayerState.Ready) {
                            val readyState = playerState as PlayerState.Ready
                            VideoControls(
                                isPlaying = player.isPlaying,
                                currentPosition = readyState.currentPosition,
                                duration = readyState.duration,
                                onPlayPauseClick = {
                                    if (player.isPlaying) {
                                        viewModel.pause()
                                    } else {
                                        viewModel.play()
                                    }
                                },
                                onForwardClick = { viewModel.seekForward() },
                                onRewindClick = { viewModel.seekBackward() },
                                onSeek = { progress ->
                                    viewModel.seekTo(progress)
                                },
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                }
                
                // Auto-hide controls after 3 seconds of inactivity
                LaunchedEffect(showControls) {
                    if (showControls) {
                        kotlinx.coroutines.delay(3000)
                        showControls = false
                    }
                }
            }
            is PlayerState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${(playerState as PlayerState.Error).message}",
                        color = Color.Red
                    )
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun VideoControls(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onForwardClick: () -> Unit,
    onRewindClick: () -> Unit,
    modifier: Modifier = Modifier,
    currentPosition: Long = 0L,
    duration: Long = 0L,
    onSeek: ((Float) -> Unit)? = null
) {
    var isSeeking by remember { mutableStateOf(false) }
    var tempProgress by remember { mutableFloatStateOf(0f) }
    
    // Update tempProgress when currentPosition changes and user is not seeking
    LaunchedEffect(currentPosition, isSeeking) {
        if (!isSeeking) {
            tempProgress = if (duration > 0) currentPosition.toFloat() / duration else 0f
        }
    }
    
    val progress = if (isSeeking) tempProgress else (if (duration > 0) currentPosition.toFloat() / duration else 0f)
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.7f)
                    )
                )
            )
            .padding(16.dp)
    ) {
        // Progress bar with time indicators
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Progress bar
            androidx.compose.material3.Slider(
                value = progress,
                onValueChange = { newProgress ->
                    isSeeking = true
                    tempProgress = newProgress
                },
                onValueChangeFinished = {
                    isSeeking = false
                    onSeek?.invoke(tempProgress)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
                )
            )
            
            // Time indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(currentPosition),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = formatDuration(duration),
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Control buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Rewind button
            IconButton(
                onClick = onRewindClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = MaterialTheme.shapes.circle
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Replay10,
                    contentDescription = "Rewind 10 seconds",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Play/Pause button
            IconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        shape = MaterialTheme.shapes.circle
                    )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            // Forward button
            IconButton(
                onClick = onForwardClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = MaterialTheme.shapes.circle
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Forward10,
                    contentDescription = "Forward 10 seconds",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    return String.format(
        "%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(durationMs),
        TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
    )
}

@Composable
private fun Row (
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable RowScope.() -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}

@Composable
private fun Icon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    androidx.compose.material3.Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}

@Composable
private fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    androidx.compose.material3.IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        content = content
    )
}
