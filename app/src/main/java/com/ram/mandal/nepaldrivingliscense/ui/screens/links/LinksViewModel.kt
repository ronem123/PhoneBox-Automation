package com.ram.mandal.nepaldrivingliscense.ui.screens.links

import androidx.lifecycle.ViewModel
import com.ram.mandal.nepaldrivingliscense.core.dispatcher.DispatcherProvider
import com.ram.mandal.nepaldrivingliscense.core.logger.Logger
import com.ram.mandal.nepaldrivingliscense.core.networkhelper.NetworkHelper
import com.ram.mandal.nepaldrivingliscense.data.repository.NepalTrialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/**
 * Created by Ram Mandal on 27/08/2025
 * @System: Apple M1 Pro
 */
@HiltViewModel
class LinksViewModel @Inject constructor(
    private val repository: NepalTrialRepository,
    private val networkHelper: NetworkHelper,
    private val logger: Logger,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

}