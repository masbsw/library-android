package com.example.library.presentation.books

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library.domain.model.Book
import com.example.library.domain.usecase.GetBooksUseCase
import com.example.library.presentation.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase
) : ViewModel() {

    private val _booksState = MutableStateFlow<UiState<List<Book>>>(UiState.Loading)
    val booksState: StateFlow<UiState<List<Book>>> = _booksState.asStateFlow()

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _booksState.value = UiState.Loading
            try {
                val result = getBooksUseCase()
                _booksState.value = if (result.isSuccess) {
                    UiState.Success(result.getOrThrow())
                } else {
                    UiState.Error("Failed to load books")
                }
            } catch (e: Exception) {
                _booksState.value = UiState.Error("Network error")
            }
        }
    }

    fun refreshBooks() {
        loadBooks()
    }
}