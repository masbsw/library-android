package com.example.library.domain.usecase

import com.example.library.data.repository.BookRepository
import javax.inject.Inject

class GetReadingStatusUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(userId: Long, bookId: Long): Result<Boolean> {
        return repository.getReadingStatus(userId, bookId)
    }
}