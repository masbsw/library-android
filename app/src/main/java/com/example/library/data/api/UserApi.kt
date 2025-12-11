package com.example.library.data.api

import com.example.library.data.dto.BookDto
import com.example.library.data.dto.UserDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<UserDto>

    @POST("api/users/{userId}/reading/{bookId}")
    suspend fun toggleReadingStatus(
        @Path("userId") userId: Long,
        @Path("bookId") bookId: Long,
        @Query("startReading") startReading: Boolean = true
    ): Response<UserDto>

    @GET("api/users/{userId}/reading")
    suspend fun getReadingBooks(@Path("userId") userId: Long): Response<List<BookDto>>



    @GET("api/users/{userId}/reading/count")
    suspend fun getReadingBooksCount(@Path("userId") userId: Long): Response<Map<String, Int>>

    @GET("api/users/{userId}/reading/{bookId}/status")
    suspend fun getReadingStatus(
        @Path("userId") userId: Long,
        @Path("bookId") bookId: Long
    ): Response<Map<String, Any>>

    @GET("api/users/{userId}/reading-books")
    suspend fun getReadingBooksForProfile(@Path("userId") userId: Long): Response<List<BookDto>>




}