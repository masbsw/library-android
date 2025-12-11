package com.example.library.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library.data.auth.AuthManager
import com.example.library.domain.model.User
import com.example.library.domain.usecase.RegisterUseCase
import com.example.library.presentation.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val authManager: AuthManager
) : ViewModel() {

    private val _registerState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val registerState: StateFlow<UiState<User>> = _registerState.asStateFlow()

    fun register(
        email: String,
        password: String,
        username: String,
        phone: String,
        name: String
    ) {
        viewModelScope.launch {
            _registerState.value = UiState.Loading
            try {
                val result = registerUseCase(email, password, username, phone, name)
                if (result.isSuccess) {
                    val user = result.getOrThrow()
                    authManager.currentUser = user // Сохраняем пользователя
                    _registerState.value = UiState.Success(user)
                } else {
                    _registerState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
                }
            } catch (e: Exception) {
                _registerState.value = UiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun resetState() {
        _registerState.value = UiState.Idle
    }
}