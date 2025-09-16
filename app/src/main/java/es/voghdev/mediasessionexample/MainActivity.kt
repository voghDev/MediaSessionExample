package es.voghdev.mediasessionexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import es.voghdev.mediasessionexample.ui.theme.MediaSessionExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediaSessionExampleTheme {
                VideoPlayerScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}