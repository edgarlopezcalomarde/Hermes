package com.edloca.hermes.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.edloca.hermes.data.models.*
import com.edloca.hermes.data.network.AuthRepositoryImp
import com.edloca.hermes.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repostory:AuthRepositoryImp
):ViewModel() {

    private val _registerResponse = MutableLiveData<UiState<String>>()
    val registerResponse: LiveData<UiState<String>> get() = _registerResponse


    fun register(email: String, password: String, user: UserModel) {
         _registerResponse.value = UiState.Loading
         repostory.registerUser(email = email, password = password, user = user) {
            _registerResponse.value= it
        }
    }

}