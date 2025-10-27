package com.phone_box_app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phone_box_app.core.dispatcher.DispatcherProvider
import com.phone_box_app.core.exception.NoInternetException
import com.phone_box_app.core.logger.Logger
import com.phone_box_app.core.networkhelper.NetworkHelper
import com.phone_box_app.data.model.DeviceRegistrationPostData
import com.phone_box_app.data.model.RegisterDeviceResponse
import com.phone_box_app.data.model.ScheduledTaskResponse
import com.phone_box_app.data.repository.ArcRepository
import com.phone_box_app.data.room.deviceinfo.DeviceInfoEntity
import com.phone_box_app.ui.UIState
import com.phone_box_app.util.getMyDeviceModel
import com.phone_box_app.util.getMyDeviceName
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


    /**
     * Holds the Response from the remote api and store in the private variable [_registerDeviceResponse]
     * interaction is performed on [registerDeviceResponse] which is just a StateFlow and it cannot be changed
     * from the other world
     */
    private val _registerDeviceResponse =
        MutableStateFlow<UIState<RegisterDeviceResponse>>(UIState.Empty)
    val registerDeviceResponse: StateFlow<UIState<RegisterDeviceResponse>> = _registerDeviceResponse


    /**
     * Holds the Response from the remote api and store in the private variable [_deviceDetailResponse]
     * interaction is performed on [deviceDetailResponse] which is just a StateFlow and it cannot be changed
     * from the other world
     */
    private val _deviceDetailResponse =
        MutableStateFlow<UIState<RegisterDeviceResponse>>(UIState.Empty)
    val deviceDetailResponse: StateFlow<UIState<RegisterDeviceResponse>> = _deviceDetailResponse


    private val _deviceInfo = MutableStateFlow<DeviceInfoEntity?>(null)
    val deviceInfo: StateFlow<DeviceInfoEntity?> = _deviceInfo

    init {
        viewModelScope.launch {
            _deviceInfo.value = repository.getLocalDeviceInfo()
        }
    }

    /**
     * Return the user entered phone-number
     */
    fun getPhoneNumber(): String = ""


    /**
     * Returns the self device information
     */
    fun getDeviceRegisterData(
        deviceId: String,
        countryCode: String,
        mobileNumber: String
    ): DeviceRegistrationPostData {

        return DeviceRegistrationPostData(
            countryCode = countryCode,
            deviceId = deviceId,
            deviceName = getMyDeviceName(),
            deviceModel = getMyDeviceModel(),
            deviceSimNumber = mobileNumber,
            profileName = getPhoneNumber(),
        )
    }

    /**
     * Method to register new device when its first time
     */
    fun registerDevice(deviceId: String, countryCode: String, mobileNumber: String) {
        viewModelScope.launch {
            if (!networkHelper.isNetworkConnected()) {
                _registerDeviceResponse.emit(UIState.Failure(throwable = NoInternetException()))
                return@launch
            }
            repository.registerDevice(getDeviceRegisterData(deviceId, countryCode, mobileNumber))
                .onStart { _registerDeviceResponse.emit(UIState.Loading) }
                .flowOn(dispatcherProvider.io)
                .catch { _registerDeviceResponse.emit(UIState.Failure(it)) }
                .collect {
                    _registerDeviceResponse.emit(UIState.Success(it))
                    _deviceInfo.value = repository.getLocalDeviceInfo()
                }
        }
    }

//    /**
//     * Get my DeviceId
//     */
//    fun getMyDeviceDetail(deviceId: String) {
//        viewModelScope.launch {
//            repository.
//        }
//    }


    /**
     * Get list of scheduled tasks
     */
    fun getScheduledTask() {
        viewModelScope.launch {
            if (!networkHelper.isNetworkConnected()) {
                _scheduledTaskResponse.emit(UIState.Failure(throwable = NoInternetException()))
                return@launch
            }

            repository.getLocalDeviceInfo()?.deviceIdInt?.let { deviceId ->
                repository.getScheduledTask(deviceId = deviceId)
                    .onStart { _scheduledTaskResponse.emit(UIState.Loading) }
                    .flowOn(dispatcherProvider.io)
                    .catch { _scheduledTaskResponse.emit(UIState.Failure(it)) }
                    .collect {
                        _scheduledTaskResponse.emit(UIState.Success(it))
                        logger.d("TAG", "SUCCESS")
                    }
            }
        }
    }
}





