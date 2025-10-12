package com.ram.mandal.nepaldrivingliscense.ui.screens.document_required

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ram.mandal.nepaldrivingliscense.data.model.NewsItem
import com.ram.mandal.nepaldrivingliscense.data.model.getNewsItem
import com.ram.mandal.nepaldrivingliscense.ui.UIState
import com.ram.mandal.nepaldrivingliscense.ui.components.ErrorComposableLayout
import com.ram.mandal.nepaldrivingliscense.ui.components.LoadingComposeLayout
import com.ram.mandal.nepaldrivingliscense.ui.components.TextComponents
import com.ram.mandal.nepaldrivingliscense.ui.screens.home.GoogleAddLayout
import com.ram.mandal.nepaldrivingliscense.ui.theme.AppThemeColor


/**
 * Created by Ram Mandal on 11/02/2024
 * @System: Apple M1 Pro
 */
@Composable
fun DocumentRequiredScreen(
    viewModel: DocumentRequiredViewModel = hiltViewModel()
) {
    val newsDetailState: UIState<NewsItem> by viewModel.newsDetail.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
//        viewModel.getNewsDetail(newsId = newsId)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppThemeColor.white)
            .verticalScroll(rememberScrollState())
    ) {
        when (newsDetailState) {
            is UIState.Loading -> {
                LoadingComposeLayout()
            }

            is UIState.Success -> {
                val newsItem = (newsDetailState as UIState.Success<NewsItem>).data
                NewsSuccess(modifier = Modifier.weight(1f), newsItem = newsItem)
            }

            is UIState.Failure -> {
                ErrorComposableLayout(errorMessage = (newsDetailState as UIState.Failure<NewsItem>).throwable?.message.toString())
            }

            is UIState.Empty -> {}
        }
        GoogleAddLayout()
    }
}

@Composable
fun NewsSuccess(modifier: Modifier, newsItem: NewsItem) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AppThemeColor.white)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        TextComponents(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            text = newsItem.title,
            typography = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextComponents(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            text = newsItem.content,
            typography = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
@Preview
fun DetailScreenPrev() {
    DocumentRequiredScreen()
}
