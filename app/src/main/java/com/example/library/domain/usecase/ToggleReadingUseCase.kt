package com.example.library.domain.usecase

import com.example.library.data.repository.BookRepository
import javax.inject.Inject

class ToggleReadingUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend fun toggleReadingStatus(userId: Long, bookId: Long, isCurrentlyReading: Boolean): Result<Boolean> {
        return try {
            val result = if (isCurrentlyReading) {
                repository.stopReadingBook(userId, bookId)
            } else {
                repository.startReadingBook(userId, bookId)
            }

            if (result.isSuccess) {
                Result.success(!isCurrentlyReading)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Toggle failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}