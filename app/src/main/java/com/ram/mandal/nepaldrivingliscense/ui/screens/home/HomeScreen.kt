package com.ram.mandal.nepaldrivingliscense.ui.screens.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ram.mandal.nepaldrivingliscense.NavigateActivity
import com.ram.mandal.nepaldrivingliscense.util.MyIntent
import com.ram.mandal.nepaldrivingliscense.data.model.NewsResponse
import com.ram.mandal.nepaldrivingliscense.data.model.getLearningMenu
import com.ram.mandal.nepaldrivingliscense.data.model.getMainMenuItem
import com.ram.mandal.nepaldrivingliscense.data.model.getPracticeMenu
import com.ram.mandal.nepaldrivingliscense.ui.UIState
import com.ram.mandal.nepaldrivingliscense.ui.components.ErrorComposableLayout
import com.ram.mandal.nepaldrivingliscense.ui.components.LoadingComposeLayout
import com.ram.mandal.nepaldrivingliscense.ui.routes.AppRoutes
import com.ram.mandal.nepaldrivingliscense.ui.theme.AppThemeColor


/**
 * Created by Ram Mandal on 25/01/2024
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
    val newsState: UIState<NewsResponse> by viewModel.newsResponse.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.getNews()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppThemeColor.white)
            .verticalScroll(rememberScrollState())
    ) {
        when (newsState) {
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
//                val  err = (newsState as UIState.Failure)
//                err.throwable.toString()
                Log.v("TAG", "Empty")

            }
        }
        DateLayout()
        MainCategory(getMainMenuItem()) { item ->
            NavigateActivity.launch(context, item.menuType)
        }
        Spacer(modifier = Modifier.height(10.dp))

        GoogleAddLayout()

        Spacer(modifier = Modifier.height(10.dp))

        SecondaryMenuBlock(groupName = "Learning", getLearningMenu()) {
        }
        Spacer(modifier = Modifier.height(10.dp))

        SecondaryMenuBlock(groupName = "Practice", getPracticeMenu()) {
        }
        Spacer(modifier = Modifier.height(10.dp))

    }
}
