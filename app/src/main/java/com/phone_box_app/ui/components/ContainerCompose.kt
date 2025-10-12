package com.phone_box_app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Created by Ram Mandal on 09/10/2025
 * @System: Apple M1 Pro
 */

/**
 * Created by Ram Mandal on 27/08/2025
 * @System: Apple M1 Pro
 */

@Composable
fun ContainerLayout(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Scrollable content takes all remaining space
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            content()
        }

    }
}
