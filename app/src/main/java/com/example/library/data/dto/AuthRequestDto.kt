package com.example.library.data.dto

data class AuthRequestDto(
    val email: String,
    val password: String,
    val username: String? = null,
    val name: String? = null,
    val phone: String? = null
)