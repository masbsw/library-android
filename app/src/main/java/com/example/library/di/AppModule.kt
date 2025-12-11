package com.example.library.di

import android.content.Context
import com.example.library.data.api.AuthApi
import com.example.library.data.api.BookApi
import com.example.library.data.api.UserApi
import com.example.library.data.auth.AuthManager
import com.example.library.data.repository.BookRepository
import com.example.library.data.repository.BookRepositoryImpl
import com.example.library.domain.usecase.GetBooksUseCase
import com.example.library.domain.usecase.GetBookUseCase
import com.example.library.domain.usecase.GetReadingBooksForProfileUseCase
import com.example.library.domain.usecase.GetReadingStatusUseCase
import com.example.library.domain.usecase.GetUserUseCase
import com.example.library.domain.usecase.ToggleReadingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8089/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBookApi(retrofit: Retrofit): BookApi {
        return retrofit.create(BookApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthManager(@ApplicationContext context: Context): AuthManager {
        return AuthManager(context)
    }

    @Provides
    @Singleton
    fun provideBookRepository(
        bookApi: BookApi,
        authApi: AuthApi,
        userApi: UserApi
    ): BookRepository {
        return BookRepositoryImpl(bookApi, authApi, userApi)
    }

    @Provides
    @Singleton
    fun provideGetBooksUseCase(repository: BookRepository): GetBooksUseCase {
        return GetBooksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetBookUseCase(repository: BookRepository): GetBookUseCase {
        return GetBookUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetUserUseCase(repository: BookRepository): GetUserUseCase {
        return GetUserUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideToggleReadingUseCase(repository: BookRepository): ToggleReadingUseCase {
        return ToggleReadingUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetReadingStatusUseCase(repository: BookRepository): GetReadingStatusUseCase {
        return GetReadingStatusUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetReadingBooksForProfileUseCase(
        repository: BookRepository
    ): GetReadingBooksForProfileUseCase {
        return GetReadingBooksForProfileUseCase(repository)
    }
}