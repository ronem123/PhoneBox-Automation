package com.phone_box_app.ui.screens.home

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.phone_box_app.data.model.MobileNumberInfo
import com.phone_box_app.ui.UIState
import com.phone_box_app.ui.components.ErrorComposableLayout
import com.phone_box_app.ui.components.LoadingComposeLayout
import com.phone_box_app.ui.components.arch_dialogs.MobileNumberInputDialog
import com.phone_box_app.ui.theme.AppThemeColor
import com.phone_box_app.util.RequestAppPermissions
import com.phone_box_app.util.getMyDeviceId


/**
 * Created by Ram Mandal on 12/10/2025
 * @System: Apple M1 Pro
 */


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    var isPermissionGranted by remember { mutableStateOf(false) }

    if (!isPermissionGranted) {
        RequestAppPermissions(
            onAllPermissionsGranted = { isPermissionGranted = true }
        )
    } else {
        HomeContent(viewModel, context)
    }
}


@Composable
fun HomeContent(viewModel: HomeViewModel, context: Context) {

    var hasAskedPermission by remember { mutableStateOf(false) }
    var mobileNumberInfo by remember { mutableStateOf(MobileNumberInfo("", "")) }

    val deviceInfoState by viewModel.deviceInfo.collectAsStateWithLifecycle()
    val deviceRegisterState by viewModel.registerDeviceResponse.collectAsStateWithLifecycle()

    var showMobileDialog by remember(deviceInfoState, hasAskedPermission) {
        mutableStateOf(hasAskedPermission && deviceInfoState?.isRegistered != true)
    }


    if (!hasAskedPermission) {
        // Ask permission only once
        RequestAppPermissions(
            onAllPermissionsGranted = {
                hasAskedPermission = true
            }
        )
    } else if (showMobileDialog) {
        MobileNumberInputDialog(
            onConfirm = { mobileNumberData ->
                showMobileDialog = false
                mobileNumberInfo = mobileNumberData
                viewModel.registerDevice(
                    deviceId = getMyDeviceId(context),
                    countryCode = mobileNumberInfo.countryCode,
                    mobileNumber = mobileNumberInfo.mobileNumber
                )
            },
            onDismiss = {
            }
        )
    } else {
        //get scheduled task
        viewModel.getScheduledTask()

        // Show main UI only after permissions + number input
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppThemeColor.grey5)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            when {
                deviceRegisterState is UIState.Loading -> LoadingComposeLayout()
                deviceRegisterState is UIState.Success
                        || deviceInfoState?.isRegistered == true -> HomeContentLayout()

                deviceRegisterState is UIState.Failure -> ErrorComposableLayout(errorMessage = "Error Occurred")
                deviceRegisterState is UIState.Empty -> Log.v("TAG", "Empty")
            }
        }
    }
}
