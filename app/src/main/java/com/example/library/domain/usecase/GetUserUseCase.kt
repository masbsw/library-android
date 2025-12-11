package com.example.library.domain.usecase

import com.example.library.data.repository.BookRepository
import com.example.library.domain.model.User
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(id: Long): Result<User> {
        return repository.getUser(id)
    }
}