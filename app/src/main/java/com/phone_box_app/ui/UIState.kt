package com.phone_box_app.ui

/**
 * Created by Ram Mandal on 12/10/2025
 * @System: Apple M1 Pro
 */
sealed interface UIState<out T> {
    data class Success<T>(val data: T) : UIState<T>
    data class Failure<T>(val throwable: Throwable? = null, val data: T? = null) : UIState<T>
    object Loading : UIState<Nothing>
    object Empty : UIState<Nothing>
}
