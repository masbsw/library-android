package com.example.library.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library.data.auth.AuthManager
import com.example.library.domain.model.Book
import com.example.library.domain.model.User
import com.example.library.domain.usecase.GetReadingBooksForProfileUseCase
import com.example.library.domain.usecase.GetUserUseCase
import com.example.library.presentation.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getReadingBooksForProfileUseCase: GetReadingBooksForProfileUseCase,
    private val authManager: AuthManager
) : ViewModel() {

    private val _userState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val userState: StateFlow<UiState<User>> = _userState.asStateFlow()

    private val _readingBooksState = MutableStateFlow<UiState<List<Book>>>(UiState.Loading)
    val readingBooksState: StateFlow<UiState<List<Book>>> = _readingBooksState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(authManager.isLoggedIn)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        if (authManager.isLoggedIn) {
            loadUser()
            loadReadingBooks()
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            if (!authManager.isLoggedIn) {
                _isLoggedIn.value = false
                return@launch
            }

            _userState.value = UiState.Loading
            try {
                val currentUser = authManager.currentUser
                if (currentUser != null) {
                    val result = getUserUseCase(currentUser.id)
                    _userState.value = if (result.isSuccess) {
                        UiState.Success(result.getOrThrow())
                    } else {
                        UiState.Success(currentUser)
                    }
                } else {
                    _userState.value = UiState.Error("User not found")
                    _isLoggedIn.value = false
                }
            } catch (e: Exception) {
                _userState.value = UiState.Error("Error loading profile")
            }
        }
    }

    fun loadReadingBooks() {
        viewModelScope.launch {
            val userId = authManager.getCurrentUserId()
            if (userId == 0L) return@launch

            _readingBooksState.value = UiState.Loading
            try {
                val result = getReadingBooksForProfileUseCase(userId)
                _readingBooksState.value = if (result.isSuccess) {
                    UiState.Success(result.getOrThrow())
                } else {
                    UiState.Error("Error loading reading books")
                }
            } catch (e: Exception) {
                _readingBooksState.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun logout() {
        authManager.logout()
        _isLoggedIn.value = false
        _userState.value = UiState.Loading
        _readingBooksState.value = UiState.Loading
    }

    fun updateLoginStatus() {
        _isLoggedIn.value = authManager.isLoggedIn
        if (authManager.isLoggedIn) {
            loadUser()
            loadReadingBooks()
        }
    }
}