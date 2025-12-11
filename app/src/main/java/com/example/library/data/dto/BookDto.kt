package com.example.library.data.dto

import com.google.gson.annotations.SerializedName


data class BookDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("description") val description: String,
    @SerializedName("cover_url") val coverUrl: String? = null,
    @SerializedName("year") val year: Int,
    @SerializedName("pages") val pages: Int,
    @SerializedName("average_rating") val averageRating: Double,
    @SerializedName("is_available") val isAvailable: Boolean,
    @SerializedName("isReading") val isReading: Boolean = false
)

