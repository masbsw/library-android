package com.example.library.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library.data.auth.AuthManager
import com.example.library.domain.model.User
import com.example.library.domain.usecase.LoginUseCase
import com.example.library.presentation.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val authManager: AuthManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val loginState: StateFlow<UiState<User>> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val result = loginUseCase(email, password)
                if (result.isSuccess) {
                    val user = result.getOrThrow()
                    authManager.currentUser = user // Сохраняем пользователя
                    _loginState.value = UiState.Success(user)
                } else {
                    _loginState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Login failed")
                }
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun resetState() {
        _loginState.value = UiState.Idle
    }
}