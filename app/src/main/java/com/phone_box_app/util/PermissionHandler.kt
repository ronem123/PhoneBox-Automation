package com.phone_box_app.util

import android.Manifest
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.accompanist.permissions.*
import androidx.compose.runtime.Composable

/**
 * Created by Ram Mandal on 15/10/2025
 * @System: Apple M1 Pro
 */

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestAppPermissions(
    onAllPermissionsGranted: () -> Unit
) {
    val permissions = listOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE
    )

    val multiplePermissionsState = rememberMultiplePermissionsState(permissions)

    //  Automatically request permissions when composable loads
    LaunchedEffect(Unit) {
        multiplePermissionsState.launchMultiplePermissionRequest()
    }

    when {
        multiplePermissionsState.allPermissionsGranted -> {
            onAllPermissionsGranted()
        }

        multiplePermissionsState.shouldShowRationale -> {
            PermissionRationaleUI {
                multiplePermissionsState.launchMultiplePermissionRequest()
            }
        }

        else -> {
            PermissionRequestUI {
                multiplePermissionsState.launchMultiplePermissionRequest()
            }
        }
    }
}

@Composable
fun PermissionRationaleUI(onRequestPermission: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Permissions Needed") },
        text = { Text("We need SMS, Call Log, and Phone permissions to make the app work properly.") },
        confirmButton = {
            TextButton(onClick = onRequestPermission) {
                Text("Grant Now")
            }
        }
    )
}

@Composable
fun PermissionRequestUI(onRequestPermission: () -> Unit) {
    Button(onClick = onRequestPermission) {
        Text("Allow Permission")
    }
}
