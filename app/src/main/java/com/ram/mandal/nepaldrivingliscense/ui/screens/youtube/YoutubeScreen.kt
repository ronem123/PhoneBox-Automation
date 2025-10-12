package com.ram.mandal.nepaldrivingliscense.ui.screens.youtube

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ram.mandal.nepaldrivingliscense.data.model.MyVideo
import com.ram.mandal.nepaldrivingliscense.ui.components.TextComponents
import io.github.ilyapavlovskii.multiplatform.youtubeplayer.SimpleYouTubePlayerOptionsBuilder
import io.github.ilyapavlovskii.multiplatform.youtubeplayer.YouTubePlayer
import io.github.ilyapavlovskii.multiplatform.youtubeplayer.YouTubeVideoId
import io.github.ilyapavlovskii.multiplatform.youtubeplayer.model.YouTubeEvent
import io.github.ilyapavlovskii.multiplatform.youtubeplayer.model.YouTubeExecCommand


/**
 * Created by Ram Mandal on 08/02/2024
 * @System: Apple M1 Pro
 */
@Composable
fun YoutubeScreen(video: MyVideo?) {
    val execCommand = remember {
        mutableStateOf<YouTubeExecCommand?>(null)
    }
    var videoDuration: String by remember { mutableStateOf("00:00") }
    var currentTime: String by remember { mutableStateOf("00:00") }

    Column {
        YouTubePlayer(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            options = SimpleYouTubePlayerOptionsBuilder.builder {
                autoplay(true)
                controls(true).rel(false)
                ivLoadPolicy(false)
                ccLoadPolicy(false)
                fullscreen = true
            },
            execCommandState = execCommand,
            actionListener = { action ->
                when (action) {
                    YouTubeEvent.Ready -> {
                        execCommand.value = YouTubeExecCommand.LoadVideo(
                            videoId = YouTubeVideoId(video?.id ?: ""),
                        )
                    }

                    is YouTubeEvent.VideoDuration,
                    is YouTubeEvent.TimeChanged,
                    is YouTubeEvent.OnVideoIdHandled,
                    is YouTubeEvent.Error,
                    is YouTubeEvent.PlaybackQualityChange,
                    is YouTubeEvent.RateChange,
                    is YouTubeEvent.StateChanged,
                    -> println("webViewState. onAction HANDlED: $action")
                }
            },
        )
        TextComponents(
            text = "${video?.name.toString()} : ${video?.url.toString()}",
            typography = MaterialTheme.typography.headlineSmall
        )
    }
}