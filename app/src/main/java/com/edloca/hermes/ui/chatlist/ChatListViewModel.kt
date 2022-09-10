package com.edloca.hermes.ui.chatlist


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edloca.hermes.data.models.UserModel
import com.edloca.hermes.data.network.AuthRepositoryImp
import com.edloca.hermes.data.network.FirebaseFirebaseFirestoreRepositoryImpl
import com.edloca.hermes.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val repositoryFirestore: FirebaseFirebaseFirestoreRepositoryImpl,
    private val authRepositoryImp: AuthRepositoryImp,

    ): ViewModel() {


    var isLoading:MutableLiveData<Boolean> = MutableLiveData()


    private val _addFriend:MutableLiveData<UiState<String>> = MutableLiveData()
    val addFriend: LiveData<UiState<String>> = _addFriend


    private val _friendsList:MutableLiveData<List<UserModel>> = MutableLiveData()
    val friendsList: LiveData<List<UserModel>> =_friendsList

    fun logOut() = authRepositoryImp.logout { }


    fun changeState(state:String){
        repositoryFirestore.changeUserState(state)
    }


    fun getFriendsList(){
        isLoading.postValue(true)
        viewModelScope.launch {
            repositoryFirestore.getFriendList {
                _friendsList.value = it
                isLoading.postValue(false)
            }
        }
    }


    fun addFriend(friendTag: String){
        viewModelScope.launch {
            repositoryFirestore.addFriend(friendTag){
                _addFriend.value = it
            }
        }
    }





}