package com.edloca.hermes.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edloca.hermes.data.models.UserModel
import com.edloca.hermes.data.network.FirebaseFirebaseFirestoreRepositoryImpl
import com.edloca.hermes.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository:FirebaseFirebaseFirestoreRepositoryImpl
):ViewModel() {

    private val _userPhoto: MutableLiveData<UiState<Uri>> = MutableLiveData()
    val userPhoto: LiveData<UiState<Uri>> = _userPhoto

    private val _userName: MutableLiveData<UiState<String>> = MutableLiveData()
    val userName: LiveData<UiState<String>> = _userName

    private val _userEmail: MutableLiveData<UiState<String>> = MutableLiveData()
    val userEmail: LiveData<UiState<String>> = _userEmail

    private val _userEmailSend: MutableLiveData<UiState<String>> = MutableLiveData()
    val userEmailSend: LiveData<UiState<String>> = _userEmailSend

    private val  _userModelInfo: MutableLiveData<UserModel> = MutableLiveData()
    val userModelInfo: LiveData<UserModel> = _userModelInfo

    fun getUserInfo(){
        viewModelScope.launch {
           repository.getUser{
               _userModelInfo.postValue(it)
           }

        }

    }


    fun resetPass(){
        repository.sendEmailForResetPass{
            _userEmailSend.postValue(it)
        }
    }


    //Cambiar img
    fun updateProfilePhoto(img: Uri?){
        viewModelScope.launch {
            if (img != null){
                repository.uploadImgProfile(img){
                    _userPhoto.postValue(it)

                }
            }
        }
    }



    fun updateUsername(username: String?){
        viewModelScope.launch {
            if (username != null){
                repository.updateUsername(username){
                    _userName.postValue(it)
                }
            }
        }
    }


    fun updateEmail(email: String?){
        viewModelScope.launch {
            if (email != null){
                repository.updateEmail(email){
                    _userEmail.postValue(it)

                }
            }
        }
    }




}