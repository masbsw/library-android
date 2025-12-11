package com.example.library.data.api

import com.example.library.data.dto.AuthRequestDto
import com.example.library.data.dto.AuthResponseDto
import com.example.library.data.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/users/register")
    suspend fun register(@Body request: AuthRequestDto): Response<AuthResponseDto>

    @POST("api/users/login")
    suspend fun login(@Body request: AuthRequestDto): Response<AuthResponseDto>
}