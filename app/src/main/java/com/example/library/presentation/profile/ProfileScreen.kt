package com.example.library.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.background
import com.example.library.presentation.ui.UiState

@Composable
fun ProfileScreen(
    onNavigateToAuth: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userState by viewModel.userState.collectAsState()
    val readingBooksState by viewModel.readingBooksState.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (!isLoggedIn) {
            NotLoggedInView(onNavigateToAuth = onNavigateToAuth)
        } else {
            when {
                userState is UiState.Loading || readingBooksState is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                userState is UiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = (userState as UiState.Error).message)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadUser() }) {
                            Text("Попробовать снова")
                        }
                    }
                }

                userState is UiState.Success -> {
                    val user = (userState as UiState.Success<com.example.library.domain.model.User>).data
                    val books = when (readingBooksState) {
                        is UiState.Success -> (readingBooksState as UiState.Success<List<com.example.library.domain.model.Book>>).data
                        is UiState.Error -> emptyList()
                        else -> emptyList()
                    }

                    ProfileContentView(
                        user = user,
                        readingBooks = books,
                        onLogout = { viewModel.logout() }
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
fun ProfileContentView(
    user: com.example.library.domain.model.User,
    readingBooks: List<com.example.library.domain.model.Book>,
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Профиль",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Информация о пользователе
        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Имя: ${user.name}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Email: ${user.email}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Телефон: ${user.phone}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Секция "Читаемые книги"
        Text(
            text = "Читаемые книги (${readingBooks.size})",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (readingBooks.isEmpty()) {
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Нет читаемых книг",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Начните читать книги из каталога",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(readingBooks) { book ->
                    ReadingBookItem(book = book)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Выйти")
        }
    }
}

@Composable
fun ReadingBookItem(book: com.example.library.domain.model.Book) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Обложка книги
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(MaterialTheme.shapes.small)
            ) {
                if (!book.coverUrl.isNullOrEmpty()) {
                    androidx.compose.foundation.Image(
                        painter = rememberAsyncImagePainter(book.coverUrl),
                        contentDescription = "Обложка книги ${book.title}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = book.title.take(2),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Информация о книге
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    Text(
                        text = "★ ${book.averageRating}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "${book.pages} стр.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun NotLoggedInView(onNavigateToAuth: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Вы не авторизованы",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Войдите или зарегистрируйтесь, чтобы получить доступ к вашему профилю",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateToAuth,
            modifier = Modifier.width(200.dp)
        ) {
            Text("Войти / Зарегистрироваться")
        }
    }
}