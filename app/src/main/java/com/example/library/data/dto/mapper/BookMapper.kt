package com.example.library.data.dto.mapper

import com.example.library.data.dto.BookDto
import com.example.library.data.dto.UserDto
import com.example.library.domain.model.Book
import com.example.library.domain.model.User

fun BookDto.toBook(): Book {
    return Book(
        id = id,
        title = title,
        author = author,
        description = description,
        coverUrl = coverUrl,
        year = year,
        pages = pages,
        averageRating = averageRating,
        isAvailable = isAvailable,
        isReading = isReading
    )
}

fun UserDto.toUser(): User {
    return User(
        id = id,
        username = username,
        name = name,
        email = email,
        phone = phone,
        currentlyReading = currentlyReading ?: emptyList()
    )
}