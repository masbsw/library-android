package com.example.library.presentation.book

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.library.R
import com.example.library.domain.model.Book
import com.example.library.presentation.ui.UiState

@Composable
fun BookDetailScreen(
    bookId: Long,
    onBackClick: () -> Unit,
    onNavigateToAuth: () -> Unit,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    val bookState by viewModel.bookState.collectAsState()
    val isReading by viewModel.isReading.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(bookId) {
        viewModel.loadBook(bookId)
    }

    when (bookState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = (bookState as UiState.Error).message)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.loadBook(bookId) }) {
                    Text("Попробовать снова")
                }
            }
        }

        is UiState.Success -> {
            val book = (bookState as UiState.Success).data
            BookDetailContent(
                book = book,
                isReading = isReading,
                isLoading = isLoading,
                onToggleReading = { viewModel.toggleReading() },
                onBackClick = onBackClick,
                onNavigateToAuth = onNavigateToAuth  // Передаем параметр дальше
            )
        }

        else -> {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun BookDetailContent(
    book: Book,
    isReading: Boolean,
    isLoading: Boolean,
    onToggleReading: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToAuth: () -> Unit  // Добавляем параметр
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Кнопка назад
        TextButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("← Назад в каталог")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Обложка
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(MaterialTheme.shapes.medium)
        ) {
            if (book.coverUrl!!.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(book.coverUrl),
                    contentDescription = "Обложка книги",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.book_placeholder),
                    contentDescription = "Нет обложки",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Информация о книге
        Text(
            text = book.title,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Автор: ${book.author}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "★ ${book.averageRating}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "${book.year} год",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "${book.pages} стр.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = "Описание:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 24.dp)
        )

        Text(
            text = book.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Кнопка "Начать/Остановить чтение"
        Button(
            onClick = onToggleReading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Обработка...")
            } else {
                Text(
                    text = if (isReading) "Перестать читать" else "Начать читать книгу",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        if (isReading) {
            Text(
                text = "Вы читаете эту книгу",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}