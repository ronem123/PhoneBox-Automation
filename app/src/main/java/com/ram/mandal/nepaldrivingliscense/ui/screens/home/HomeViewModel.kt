package com.ram.mandal.nepaldrivingliscense.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ram.mandal.nepaldrivingliscense.core.dispatcher.DispatcherProvider
import com.ram.mandal.nepaldrivingliscense.core.exception.NoInternetException
import com.ram.mandal.nepaldrivingliscense.core.logger.Logger
import com.ram.mandal.nepaldrivingliscense.core.networkhelper.NetworkHelper
import com.ram.mandal.nepaldrivingliscense.data.model.NewsResponse
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
 * Created by Ram Mandal on 25/01/2024
 * @System: Apple M1 Pro
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NepalTrialRepository,
    private val networkHelper: NetworkHelper,
    private val logger: Logger,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    /**
     * Holds the Response from the remote api and store in the private variable [_newsResponse]
     * interaction is performed on [newsResponse] which is just a StateFlow and it cannot be changed
     * from the other world
     */
    private val _newsResponse = MutableStateFlow<UIState<NewsResponse>>(UIState.Empty)
    val newsResponse: StateFlow<UIState<NewsResponse>> = _newsResponse


//    init {
//        getNews()
//    }

    fun getNews() {
        viewModelScope.launch {
            if (!networkHelper.isNetworkConnected()) {
                _newsResponse.emit(UIState.Failure(throwable = NoInternetException()))
                return@launch
            }

            repository.getNews(country = "", pageNum = "")
                .onStart { _newsResponse.emit(UIState.Loading) }
                .flowOn(dispatcherProvider.io)
                .catch { _newsResponse.emit(UIState.Failure(it)) }
                .collect {
                    _newsResponse.emit(UIState.Success(it))
                    logger.d("TAG", "SUCCESS")
                }
        }
    }

    fun getHomeContent(){
        viewModelScope.launch {
            if (!networkHelper.isNetworkConnected()) {
                _newsResponse.emit(UIState.Failure(throwable = NoInternetException()))
                return@launch
            }


        }
    }

}





