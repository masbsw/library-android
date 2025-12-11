package com.example.library.data.dto

import com.example.library.domain.model.Book
import com.example.library.domain.model.User

data class UserWithBooksResponse(
    val user: UserDto,
    val readingBooks: List<BookDto>
)