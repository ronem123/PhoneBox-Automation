package com.ram.mandal.nepaldrivingliscense.core.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
/**
 * Created by Ram Mandal on 12/10/2025
 * @System: Apple M1 Pro
 */
interface DispatcherProvider {

    val main: CoroutineDispatcher

    val io: CoroutineDispatcher

    val default: CoroutineDispatcher

}