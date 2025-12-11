package com.example.library.data.repository

import com.example.library.domain.model.Book
import com.example.library.domain.model.User

interface BookRepository {
    suspend fun getBooks(): Result<List<Book>>
    suspend fun getBookById(id: Long): Result<Book>
    suspend fun getUser(id: Long): Result<User>
    suspend fun register(email: String, password: String, username: String, phone: String, name: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun startReadingBook(userId: Long, bookId: Long): Result<User>
    suspend fun stopReadingBook(userId: Long, bookId: Long): Result<User>
    suspend fun getReadingBooks(userId: Long): Result<List<Book>>
    suspend fun getReadingStatus(userId: Long, bookId: Long): Result<Boolean>
    suspend fun getReadingBooksForProfile(userId: Long): Result<List<Book>>

}