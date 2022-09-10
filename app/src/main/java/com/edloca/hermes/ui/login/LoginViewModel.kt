package com.edloca.hermes.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.edloca.hermes.data.models.UserModel
import com.edloca.hermes.data.network.AuthRepositoryImp
import com.edloca.hermes.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepositoryImp
):ViewModel(){

    private val _loginResponse = MutableLiveData<UiState<String>>()
    val loginResponse: LiveData<UiState<String>> get() = _loginResponse


    fun login(email: String, password: String) {
        _loginResponse.value = UiState.Loading
        repository.loginUser(email, password){
            _loginResponse.value = it
        }
    }

    fun logout(result: () -> Unit){
        repository.logout(result)
    }

    fun getSession(result: (UserModel?) -> Unit){
        repository.getSession(result)
    }


}