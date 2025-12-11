package com.example.library.domain.usecase

import com.example.library.data.repository.BookRepository
import com.example.library.domain.model.Book
import javax.inject.Inject

class GetBookUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(id: Long): Result<Book> {
        return repository.getBookById(id)
    }
}