package com.example.library.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.library.presentation.auth.AuthScreen
import com.example.library.presentation.book.BookDetailScreen
import com.example.library.presentation.books.BooksScreen
import com.example.library.presentation.profile.ProfileScreen
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue


@Composable
fun Navigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Показываем BottomNavigationBar только на главных экранах
            if (currentRoute == Screen.Books.route || currentRoute == Screen.Profile.route) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Books.route,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Books.route) {
                BooksScreen(
                    onBookClick = { bookId ->
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }
            composable(
                route = Screen.BookDetail.routeWithArgs,
                arguments = listOf(
                    androidx.navigation.navArgument(Screen.BookDetail.argBookId) {
                        type = androidx.navigation.NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getLong(Screen.BookDetail.argBookId) ?: 0L
                BookDetailScreen(
                    bookId = bookId,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }
            composable(route = Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }
            composable(route = Screen.Auth.route) {
                AuthScreen(
                    onLoginSuccess = {
                        navController.popBackStack()
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(Screen.Books.route) { inclusive = false }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}