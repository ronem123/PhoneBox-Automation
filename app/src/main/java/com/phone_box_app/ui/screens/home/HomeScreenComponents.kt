package com.phone_box_app.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.phone_box_app.R
import com.phone_box_app.ui.components.ButtonComponents
import com.phone_box_app.ui.components.TextComponents
import com.phone_box_app.ui.theme.AppThemeColor


/**
 * Created by Ram Mandal on 25/01/2024
 * @System: Apple M1 Pro
 */


@Composable
fun NotificationLayout() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .defaultMinSize(minHeight = 50.dp)
            .background(AppThemeColor.yellow10)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
                    .clip(CircleShape)
                    .background(color = AppThemeColor.yellow50)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_error_red),
                    contentDescription = "profile pic",
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp)
                        .align(Alignment.Center)
                )
            }
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(horizontal = 10.dp, vertical = 0.dp)
            ) {
                TextComponents(
                    text = "From my understanding, this removes the the linting error, as the padding parameter is \"used\"",
                    typography = MaterialTheme.typography.bodySmall
                )
                ButtonComponents(
                    modifier = Modifier.align(Alignment.End),
                    color = AppThemeColor.white,
                    buttonText = "Click to check",
                    buttonBgColor = AppThemeColor.primary,
                    height = 35.dp,
                    verticalPadding = 2.dp
                ) {

                }
            }
        }
    }
}
