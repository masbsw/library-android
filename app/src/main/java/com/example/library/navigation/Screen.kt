package com.example.library.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Books : Screen("books")
    object Profile : Screen("profile")
    object BookDetail : Screen("book_detail/{bookId}") {
        const val routeWithArgs = "book_detail/{bookId}"
        const val argBookId = "bookId"

        fun createRoute(bookId: Long) = "book_detail/$bookId"
    }
}