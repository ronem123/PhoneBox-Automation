package com.ram.mandal.nepaldrivingliscense.ui.screens.videos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ram.mandal.nepaldrivingliscense.data.model.MyVideo
import com.ram.mandal.nepaldrivingliscense.data.model.getVideos
import com.ram.mandal.nepaldrivingliscense.ui.screens.home.GoogleAddLayout
import com.ram.mandal.nepaldrivingliscense.ui.theme.AppThemeColor


/**
 * Created by Ram Mandal on 11/02/2024
 * @System: Apple M1 Pro
 */
@Composable
fun AllVideosScreen(onItemClick: (MyVideo) -> Unit) {
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(AppThemeColor.white)
        ) {
            AllVideosComponent(video = getVideos(), onItemClick = onItemClick)
        }
        GoogleAddLayout()
    }
}