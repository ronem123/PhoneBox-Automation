package com.phone_box_app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phone_box_app.core.dispatcher.DispatcherProvider
import com.phone_box_app.core.exception.NoInternetException
import com.phone_box_app.core.logger.Logger
import com.phone_box_app.core.networkhelper.NetworkHelper
import com.phone_box_app.data.model.ScheduledTaskResponse
import com.phone_box_app.data.repository.ArcRepository
import com.phone_box_app.ui.UIState
import com.phone_box_app.util.getMyDeviceId
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
    private val repository: ArcRepository,
    private val networkHelper: NetworkHelper,
    private val logger: Logger,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    /**
     * Holds the Response from the remote api and store in the private variable [_scheduledTaskResponse]
     * interaction is performed on [scheduledTaskResponse] which is just a StateFlow and it cannot be changed
     * from the other world
     */
    private val _scheduledTaskResponse =
        MutableStateFlow<UIState<ScheduledTaskResponse>>(UIState.Empty)
    val scheduledTaskResponse: StateFlow<UIState<ScheduledTaskResponse>> = _scheduledTaskResponse


//    init {
//        getNews()
//    }

    fun getNews() {
        viewModelScope.launch {
            if (!networkHelper.isNetworkConnected()) {
                _scheduledTaskResponse.emit(UIState.Failure(throwable = NoInternetException()))
                return@launch
            }

            repository.getScheduledTask(deviceId = getMyDeviceId())
                .onStart { _scheduledTaskResponse.emit(UIState.Loading) }
                .flowOn(dispatcherProvider.io)
                .catch { _scheduledTaskResponse.emit(UIState.Failure(it)) }
                .collect {
                    _scheduledTaskResponse.emit(UIState.Success(it))
                    logger.d("TAG", "SUCCESS")
                }
        }
    }

    fun getHomeContent() {
        viewModelScope.launch {
            if (!networkHelper.isNetworkConnected()) {
                _scheduledTaskResponse.emit(UIState.Failure(throwable = NoInternetException()))
                return@launch
            }


        }
    }

}





