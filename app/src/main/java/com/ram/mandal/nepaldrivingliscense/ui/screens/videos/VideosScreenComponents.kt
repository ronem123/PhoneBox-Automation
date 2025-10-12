package com.ram.mandal.nepaldrivingliscense.ui.screens.videos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ram.mandal.nepaldrivingliscense.data.model.MyVideo
import com.ram.mandal.nepaldrivingliscense.ui.components.TextComponents
import com.ram.mandal.nepaldrivingliscense.ui.components.TextComponentsWithMaxLine
import com.ram.mandal.nepaldrivingliscense.ui.theme.AppThemeColor
import androidx.compose.foundation.lazy.items



/**
 * Created by Ram Mandal on 11/02/2024
 * @System: Apple M1 Pro
 */


@Composable
fun AllVideosComponent(video: List<MyVideo>, onItemClick: (MyVideo) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 10.dp)
            .background(AppThemeColor.white)
    ) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(video) {
                VideoItemHorizontal(radio = it, onItemClick = onItemClick)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoItemHorizontal(radio: MyVideo, onItemClick: (MyVideo) -> Unit) {
    Card(
        border = BorderStroke(width = 1.dp, color = AppThemeColor.grey5),
        onClick = { onItemClick.invoke(radio) },
        modifier = Modifier
            .padding(2.dp)
            .fillMaxSize()
            .height(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(AppThemeColor.white)
        ) {
            AsyncImage(
                modifier = Modifier
                    .width(180.dp)
                    .fillMaxHeight(),
                model = radio.icon,
                contentScale = ContentScale.Crop,
                contentDescription = "Videos"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                TextComponentsWithMaxLine(
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = radio.name,
                    typography = MaterialTheme.typography.bodyLarge
                )
                TextComponents(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "02:30 min",
                    typography = MaterialTheme.typography.labelSmall
                )
            }//column
        }
    }
}