package com.example.library.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.library.presentation.ui.UiState

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    registerViewModel: RegisterViewModel = hiltViewModel()
) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    val loginState by viewModel.loginState.collectAsState()
    val registerState by registerViewModel.registerState.collectAsState()

    // Сбрасываем состояние при переключении между логином и регистрацией
    LaunchedEffect(isLogin) {
        if (isLogin) {
            registerViewModel.resetState()
        } else {
            viewModel.resetState()
        }
    }

    // Обработка успешного логина
    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            onLoginSuccess()
        }
    }

    // Обработка успешной регистрации
    LaunchedEffect(registerState) {
        if (registerState is UiState.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLogin) "Вход" else "Регистрация",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!isLogin) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Никнейм") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Телефон") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Показать ошибки
        when {
            loginState is UiState.Error && isLogin -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (loginState as UiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            registerState is UiState.Error && !isLogin -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (registerState as UiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        val isLoading = (loginState is UiState.Loading && isLogin) ||
                (registerState is UiState.Loading && !isLogin)

        Button(
            onClick = {
                if (isLogin) {
                    viewModel.login(email, password)
                } else {
                    registerViewModel.register(email, password, username, phone, name)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = when {
                isLogin -> email.isNotEmpty() && password.isNotEmpty() && !isLoading
                else -> email.isNotEmpty() && password.isNotEmpty() &&
                        username.isNotEmpty() && name.isNotEmpty() && !isLoading
            }
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isLogin) "Вход..." else "Регистрация...")
            } else {
                Text(if (isLogin) "Войти" else "Зарегистрироваться")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                isLogin = !isLogin
                // Сброс полей при переключении
                if (isLogin) {
                    username = ""
                    name = ""
                    phone = ""
                }
            }
        ) {
            Text(if (isLogin) "Нет аккаунта? Зарегистрируйтесь" else "Уже есть аккаунт? Войдите")
        }
    }
}