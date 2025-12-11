package com.example.library.presentation.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library.data.auth.AuthManager
import com.example.library.domain.model.Book
import com.example.library.domain.usecase.GetBookUseCase
import com.example.library.domain.usecase.GetReadingStatusUseCase
import com.example.library.domain.usecase.ToggleReadingUseCase
import com.example.library.presentation.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val getBookUseCase: GetBookUseCase,
    private val toggleReadingUseCase: ToggleReadingUseCase,
    private val getReadingStatusUseCase: GetReadingStatusUseCase,
    private val authManager: AuthManager
) : ViewModel() {

    private val _bookState = MutableStateFlow<UiState<Book>>(UiState.Loading)
    val bookState: StateFlow<UiState<Book>> = _bookState.asStateFlow()

    private val _isReading = MutableStateFlow(false)
    val isReading: StateFlow<Boolean> = _isReading.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentBookId: Long = 0L

    fun loadBook(bookId: Long) {
        currentBookId = bookId
        viewModelScope.launch {
            _bookState.value = UiState.Loading
            try {
                val bookResult = getBookUseCase(bookId)
                if (bookResult.isSuccess) {
                    val book = bookResult.getOrThrow()
                    _bookState.value = UiState.Success(book)

                    loadReadingStatus()
                } else {
                    _bookState.value = UiState.Error("Book not found")
                }
            } catch (e: Exception) {
                _bookState.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun toggleReading() {
        val userId = authManager.getCurrentUserId()
        if (userId == 0L) {
            println("DEBUG ERROR: User not authenticated!")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentStatus = _isReading.value
                val result = toggleReadingUseCase.toggleReadingStatus(userId, currentBookId, currentStatus)

                if (result.isSuccess) {
                    val newStatus = result.getOrThrow()
                    _isReading.value = newStatus

                    updateBookStateWithReadingStatus(newStatus)
                    println("DEBUG SUCCESS: Book status toggled to: $newStatus")
                } else {
                    println("DEBUG FAILED: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                println("DEBUG EXCEPTION: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateBookStateWithReadingStatus(isReading: Boolean) {
        val currentState = _bookState.value
        if (currentState is UiState.Success) {
            val book = currentState.data.copy(isReading = isReading)
            _bookState.value = UiState.Success(book)
        }
    }

    private suspend fun loadReadingStatus() {
        val userId = authManager.getCurrentUserId()
        if (userId == 0L) return

        try {
            val result = getReadingStatusUseCase(userId, currentBookId)
            if (result.isSuccess) {
                _isReading.value = result.getOrThrow()
                println("DEBUG: Loaded reading status: ${_isReading.value}")
            }
        } catch (e: Exception) {
            println("DEBUG: Error loading reading status: ${e.message}")
        }
    }
}
