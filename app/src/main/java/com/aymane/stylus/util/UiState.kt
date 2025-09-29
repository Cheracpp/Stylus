package com.aymane.stylus.util

/**
 * A sealed class representing the state of a UI operation.
 *
 * @param T The type of data being handled in the success state.
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Empty : UiState<Nothing>() // For initial state or no results
}
