package com.ram.mandal.nepaldrivingliscense.ui.screens.document_required

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ram.mandal.nepaldrivingliscense.core.dispatcher.DispatcherProvider
import com.ram.mandal.nepaldrivingliscense.core.exception.NoInternetException
import com.ram.mandal.nepaldrivingliscense.core.logger.Logger
import com.ram.mandal.nepaldrivingliscense.core.networkhelper.NetworkHelper
import com.ram.mandal.nepaldrivingliscense.data.model.NewsItem
import com.ram.mandal.nepaldrivingliscense.data.model.getNewsItem
import com.ram.mandal.nepaldrivingliscense.data.repository.NepalTrialRepository
import com.ram.mandal.nepaldrivingliscense.ui.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by Ram Mandal on 11/02/2024
 * @System: Apple M1 Pro
 */
@HiltViewModel
class DocumentRequiredViewModel @Inject constructor(
    private val logger: Logger,
    private val repository: NepalTrialRepository,
    private val networkHelper: NetworkHelper,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {
    private val _newsDetail = MutableStateFlow<UIState<NewsItem>>(UIState.Empty)
    val newsDetail: StateFlow<UIState<NewsItem>> = _newsDetail/*.asStateFlow()*/
    fun getNewsDetail(newsId: String) {
        viewModelScope.launch {
            if (!networkHelper.isNetworkConnected()) {
                _newsDetail.emit(UIState.Failure(throwable = NoInternetException()))
                return@launch
            }

            repository.getNewsDetail(newsId)
                .onStart { _newsDetail.emit(UIState.Loading) }
                .flowOn(dispatcherProvider.io)
                .catch { _newsDetail.emit(UIState.Failure(it)) }
                .collect {
                    val newsItem = getNewsItem()[0]
                    _newsDetail.emit(UIState.Success(newsItem))
                }
        }
    }

}