package com.example.library.domain.usecase

import com.example.library.data.repository.BookRepository
import com.example.library.domain.model.Book
import javax.inject.Inject

class GetReadingBooksForProfileUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(userId: Long): Result<List<Book>> {
        return repository.getReadingBooksForProfile(userId)
    }
}