package com.example.library.data.repository

import com.example.library.data.api.AuthApi
import com.example.library.data.api.BookApi
import com.example.library.data.api.UserApi
import com.example.library.data.dto.mapper.toBook
import com.example.library.data.dto.mapper.toUser
import com.example.library.domain.model.Book
import com.example.library.domain.model.User
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookApi: BookApi,
    private val authApi: AuthApi,
    private val userApi: UserApi
) : BookRepository {

    // Кэшированные данные (опционально)
    private var cachedBooks: List<Book>? = null

    override suspend fun getBooks(): Result<List<Book>> {
        return try {
            val response = bookApi.getBooks()
            if (response.isSuccessful) {
                val books = response.body()?.map { it.toBook() } ?: emptyList()
                cachedBooks = books
                Result.success(books)
            } else {
                Result.failure(Exception("Failed to load books: ${response.code()}"))
            }
        } catch (e: Exception) {
            // Fallback: возвращаем кэшированные данные или mock
            cachedBooks?.let {
                return Result.success(it)
            }
            Result.failure(e)
        }
    }

    override suspend fun getBookById(id: Long): Result<Book> {
        return try {
            val response = bookApi.getBookById(id)
            if (response.isSuccessful) {
                val book = response.body()?.toBook()
                if (book != null) {
                    Result.success(book)
                } else {
                    Result.failure(Exception("Book not found"))
                }
            } else {
                Result.failure(Exception("Failed to load book: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(id: Long): Result<User> {
        return try {
            val response = userApi.getUserById(id)
            if (response.isSuccessful) {
                val user = response.body()?.toUser()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("User not found"))
                }
            } else {
                Result.failure(Exception("Failed to load user: ${response.code()}"))
            }
        } catch (e: Exception) {
            // Fallback: mock пользователь
            Result.success(
                User(
                    id = id,
                    username = "testuser",
                    name = "Test User",
                    email = "test@example.com",
                    phone = "+7 (999) 123-45-67",
                    currentlyReading = emptyList()
                )
            )
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        username: String,
        phone: String,
        name: String
    ): Result<User> {
        return try {
            val request = com.example.library.data.dto.AuthRequestDto(
                email = email,
                password = password,
                username = username,
                phone = phone,
                name = name
            )

            val response = authApi.register(request)
            if (response.isSuccessful) {
                val authResponse = response.body()
                val user = authResponse?.user?.toUser()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Registration failed"))
                }
            } else {
                Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val request = com.example.library.data.dto.AuthRequestDto(
                email = email,
                password = password
            )

            val response = authApi.login(request)
            if (response.isSuccessful) {
                val authResponse = response.body()
                val user = authResponse?.user?.toUser()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Login failed"))
                }
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun stopReadingBook(userId: Long, bookId: Long): Result<User> {
        return try {
            val response = userApi.toggleReadingStatus(userId, bookId, false)
            if (response.isSuccessful) {
                val user = response.body()?.toUser()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Failed to stop reading"))
                }
            } else {
                Result.failure(Exception("Failed to stop reading: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReadingBooks(userId: Long): Result<List<Book>> {
        return try {
            val response = userApi.getReadingBooks(userId)
            if (response.isSuccessful) {
                val books = response.body()?.map { it.toBook() } ?: emptyList()
                Result.success(books)
            } else {
                Result.failure(Exception("Failed to get reading books: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun startReadingBook(userId: Long, bookId: Long): Result<User> {
        return try {
            println("DEBUG: Calling toggleReadingStatus for user $userId, book $bookId")

            val response = userApi.toggleReadingStatus(userId, bookId, true)

            println("DEBUG: Response code: ${response.code()}")
            println("DEBUG: Response isSuccessful: ${response.isSuccessful()}")

            if (response.isSuccessful) {
                val userDto = response.body()
                println("DEBUG: User DTO received: $userDto")

                val user = userDto?.toUser()
                if (user != null) {
                    println("DEBUG: Successfully converted to User: $user")
                    Result.success(user)
                } else {
                    println("DEBUG: User DTO is null or conversion failed")
                    Result.failure(Exception("Failed to parse user response"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                println("DEBUG: Error response: $errorBody")

                // Если книга уже в списке (409 или специфичная ошибка)
                if (response.code() == 400 && errorBody?.contains("уже") == true) {
                    // Книга уже добавлена - это успех для UI
                    println("DEBUG: Book already in reading list")
                    Result.success(
                        User(
                            id = userId,
                            username = "temp",
                            name = "Temp",
                            email = "temp@example.com",
                            phone = "",
                            currentlyReading = emptyList()
                        )
                    )
                } else {
                    Result.failure(Exception("Failed: ${response.code()} - $errorBody"))
                }
            }
        } catch (e: Exception) {
            println("DEBUG: Exception: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getReadingStatus(userId: Long, bookId: Long): Result<Boolean> {
        return try {
            val response = userApi.getReadingStatus(userId, bookId)
            if (response.isSuccessful) {
                val body = response.body()
                val isReading = body?.get("isReading") as? Boolean ?: false
                Result.success(isReading)
            } else {
                Result.failure(Exception("Failed to get reading status: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReadingBooksForProfile(userId: Long): Result<List<Book>> {
        return try {
            val response = userApi.getReadingBooksForProfile(userId)
            if (response.isSuccessful) {
                val books = response.body()?.map { it.toBook() } ?: emptyList()
                Result.success(books)
            } else {
                Result.failure(Exception("Failed to get reading books: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}