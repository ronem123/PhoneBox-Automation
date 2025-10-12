package com.phone_box_app.ui.screens.home

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phone_box_app.data.model.ScheduledTaskResponse
import com.phone_box_app.ui.UIState
import com.phone_box_app.ui.components.ErrorComposableLayout
import com.phone_box_app.ui.components.LoadingComposeLayout
import com.phone_box_app.ui.theme.AppThemeColor


/**
 * Created by Ram Mandal on 12/10/2025
 * @System: Apple M1 Pro
 */


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    HomeContent(viewModel)
}


@Composable
fun HomeContent(viewModel: HomeViewModel, context: Context = LocalContext.current) {
    val scheduledTaskState: UIState<ScheduledTaskResponse> by viewModel.scheduledTaskResponse.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.getNews()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppThemeColor.white)
            .verticalScroll(rememberScrollState())
    ) {
        when (scheduledTaskState) {
            is UIState.Loading -> {
                LoadingComposeLayout()
            }

            is UIState.Success -> {
                NotificationLayout()
            }

            is UIState.Failure -> {
                ErrorComposableLayout(errorMessage = "Error Occurred")
            }

            is UIState.Empty -> {
                Log.v("TAG", "Empty")

            }
        }


    }
}
