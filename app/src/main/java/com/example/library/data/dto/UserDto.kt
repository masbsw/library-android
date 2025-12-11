package com.example.library.data.dto

import com.example.library.domain.model.Book
import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: Long,
    @SerializedName("username") val username: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("currentlyReading")val currentlyReading: List<Long>? = null
)

