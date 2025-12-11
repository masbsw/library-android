package com.example.library.domain.model

data class Book(
    val id: Long,
    val title: String,
    val author: String,
    val description: String,
    val coverUrl: String?,
    val year: Int,
    val pages: Int,
    val averageRating: Double,
    val isAvailable: Boolean,
    val isReading: Boolean = false
)

