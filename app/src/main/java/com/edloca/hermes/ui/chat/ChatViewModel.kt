package com.edloca.hermes.ui.chat

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.edloca.hermes.data.models.MessageModel
import com.edloca.hermes.data.models.UserModel
import com.edloca.hermes.data.network.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messagesRepository: MessagesRepositoryImpl
) :ViewModel() {


    private val _userInfo: MutableLiveData<UserModel> = MutableLiveData()
    val userInfo: LiveData<UserModel> = _userInfo

    val imageList = MutableLiveData<MutableList<Uri>>()

    private val _messageData = MutableLiveData<List<MessageModel>>()
    val messageData : LiveData<List<MessageModel>> = _messageData

    val isStorageTaskComplete : MutableLiveData<Boolean> = MutableLiveData()

    fun setRooms(receiveruid:String){
        messagesRepository.setRoom(receiveruid)
    }

    fun getInfoUser(reciverid:String){
        viewModelScope.launch {
            messagesRepository.getUser(reciverid) {
                _userInfo.postValue(it)

            }
        }

    }


    fun fetchMessagesList( messagelist:MutableList<MessageModel>) {
         viewModelScope.launch {

             messagesRepository.getSenderRoomMessagesFromFirestore(messagelist).observeForever{  messageList ->
                 _messageData.postValue(messageList)
             }

         }

    }


    fun sendMessage(messageText:String){
        viewModelScope.launch {
           messagesRepository.sendMessage(messageText)
        }
    }


    fun sendImage(listPhothos:MutableList<Uri>){
        isStorageTaskComplete.postValue(false)

        viewModelScope.launch {

            var listPhotosX = mutableListOf<String>()

            messagesRepository.uploadMultipleFile(listPhothos){
                listPhotosX =it.toMutableList()
            }

            messagesRepository.sendImageMessage(listPhotosX)
            isStorageTaskComplete.postValue(true)

        }


    }

    fun chatId():String = messagesRepository.getCurrentUserId()







}