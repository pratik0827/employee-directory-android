package com.example.employeedetails.viewmodel

/**
 * Sealed interface representing distinct UI render states.
 */
sealed interface UiState<out T> {

    /**
     * Active loading state during network calls or initial fetch.
     */
    data object Loading : UiState<Nothing>

    /**
     * Successful data retrieval containing non-empty payload [data].
     */
    data class Success<out T>(val data: T) : UiState<T>

    /**
     * Failure state containing a human-readable [message].
     */
    data class Error(val message: String) : UiState<Nothing>

    /**
     * Empty state when request succeeded but returned 0 items.
     */
    data object Empty : UiState<Nothing>
}
