package com.example.library.domain.usecase

import com.example.library.data.repository.BookRepository
import com.example.library.domain.model.User
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return repository.login(email, password)
    }
}