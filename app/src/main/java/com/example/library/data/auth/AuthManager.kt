package com.example.library.data.auth

import android.content.Context
import androidx.core.content.edit
import com.example.library.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit { putBoolean(KEY_IS_LOGGED_IN, value) }

    var currentUser: User?
        get() {
            if (!isLoggedIn) return null
            val id = prefs.getLong(KEY_USER_ID, 0L)
            val name = prefs.getString(KEY_USER_NAME, "")
            val email = prefs.getString(KEY_USER_EMAIL, "")

            return if (id != 0L) {
                User(
                    id = id,
                    username = name ?: "",
                    name = name ?: "",
                    email = email ?: "",
                    phone = "",
                    currentlyReading = emptyList()
                )
            } else {
                null
            }
        }
        set(value) {
            value?.let { user ->
                prefs.edit {
                    putLong(KEY_USER_ID, user.id)
                    putString(KEY_USER_NAME, user.name)
                    putString(KEY_USER_EMAIL, user.email)
                    putBoolean(KEY_IS_LOGGED_IN, true)
                }
            } ?: run {
                prefs.edit {
                    remove(KEY_USER_ID)
                    remove(KEY_USER_NAME)
                    remove(KEY_USER_EMAIL)
                    putBoolean(KEY_IS_LOGGED_IN, false)
                }
            }
        }

    fun logout() {
        prefs.edit {
            clear()
        }
    }




    fun getCurrentUserId(): Long {
        return prefs.getLong(KEY_USER_ID, 0L)
    }




}