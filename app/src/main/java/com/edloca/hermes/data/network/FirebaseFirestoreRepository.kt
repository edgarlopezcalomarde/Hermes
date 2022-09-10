package com.edloca.hermes.data.network

import android.net.Uri
import com.edloca.hermes.data.models.UserModel
import com.edloca.hermes.utils.UiState

interface FirebaseFirestoreRepository {

    suspend fun updateProfile( fields:Map<String, String>,result:(UiState<String>) -> Unit )
    suspend fun addFriend(friendTag:String, result: (UiState<String>) -> Unit)
    suspend fun getFriendList( result: (List<UserModel>) -> Unit)
    suspend fun updateUsername(fieldContent: String, result: (UiState<String>) -> Unit)
    suspend fun updateEmail(fieldContent: String, result: (UiState<String>) -> Unit)


    suspend fun uploadImgProfile(fileUri: Uri, onResult: (UiState<Uri>) -> Unit)

    fun sendEmailForResetPass(result: (UiState<String>) -> Unit)

    fun getUsersList(result: (UiState<List<UserModel>>) -> Unit)
    suspend fun getUser(result: (UserModel)-> Unit)
    fun changeUserState(userState:String)

}