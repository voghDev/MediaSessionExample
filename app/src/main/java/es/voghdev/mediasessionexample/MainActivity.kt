package es.voghdev.mediasessionexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import es.voghdev.mediasessionexample.feature.videoplayer.VideoPlayerScreen
import es.voghdev.mediasessionexample.feature.videoplayer.VideoPlayerViewModel
import es.voghdev.mediasessionexample.ui.theme.NoActionBarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoActionBarTheme {
                val viewModel: VideoPlayerViewModel = viewModel()
                VideoPlayerScreen(
                    viewModel = viewModel,
                    onBack = { finish() }
                )
            }
        }
    }
}