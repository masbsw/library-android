package com.example.library.domain.model

data class User(
    val id: Long,
    val username: String,
    val name: String,
    val email: String,
    val phone: String,
    val currentlyReading: List<Long> = emptyList()
)