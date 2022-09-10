package com.edloca.hermes.data.network

import com.edloca.hermes.utils.UiState
import com.edloca.hermes.data.models.UserModel


interface AuthRepository {
    fun registerUser(email: String, password: String, user: UserModel, result: (UiState<String>) -> Unit)
    fun updateUserInfo(user: UserModel, result: (UiState<String>) -> Unit)
    fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit)
    fun forgotPassword(email: String, result: (UiState<String>) -> Unit)
    fun logout(result: () -> Unit)
    fun storeSession(id: String, result: (UserModel?) -> Unit)
    fun getSession(result: (UserModel?) -> Unit)
}