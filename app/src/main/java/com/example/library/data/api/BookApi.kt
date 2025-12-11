package com.example.library.data.api

import com.example.library.data.dto.BookDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApi {
    @GET("api/books")
    suspend fun getBooks(): Response<List<BookDto>>

    @GET("api/books/{id}")
    suspend fun getBookById(@Path("id") id: Long): Response<BookDto>

    // Добавьте если есть на бэкенде
    @GET("api/books/available")
    suspend fun getAvailableBooks(): Response<List<BookDto>>

    // Поиск книг
    @GET("api/books/search")
    suspend fun searchBooks(@Query("query") query: String): Response<List<BookDto>>
}