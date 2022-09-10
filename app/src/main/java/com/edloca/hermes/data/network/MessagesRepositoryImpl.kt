package com.edloca.hermes.data.network

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.edloca.hermes.data.models.MessageModel
import com.edloca.hermes.data.models.UserModel
import com.edloca.hermes.ui.chat.RecyclerMessage
import com.edloca.hermes.utils.FireStoreCollections
import com.edloca.hermes.utils.FirebaseStorageConstants
import com.google.firebase.FirebaseException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class MessagesRepositoryImpl @Inject constructor(
    private val auth:FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage:FirebaseStorage
):MessageRepository {

    private var senderRoom:String? = null
    private var recieverRoom:String?= null

    override suspend fun uploadMultipleFile(
        fileUri: List<Uri>,
        onResult: (List<String>) -> Unit
    ) {
        try {
            val uri: List<Uri> = withContext(Dispatchers.IO) {
                fileUri.map { image ->
                    async {
                        storage.reference
                            .child(auth.currentUser?.uid!!)
                            .child(FirebaseStorageConstants.MESSAGE_IMAGE)
                            .child(image.lastPathSegment ?: "${System.currentTimeMillis()}")
                            .putFile(image)
                            .await()
                            .storage
                            .downloadUrl
                            .await()
                    }
                }.awaitAll()
            }

            onResult.invoke(uriPhotoToUrl(uri))
        } catch (e: FirebaseException){

        }catch (e: Exception){

        }
    }



    fun getSenderRoomMessagesFromFirestore( messageModelList:MutableList<MessageModel>): LiveData<List<MessageModel>> {
        val mutableData = MutableLiveData<List<MessageModel>>()
        db.collection(FireStoreCollections.CHATS).document(senderRoom!!).collection(FireStoreCollections.MESSAGES)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener (object :
                EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?)
                {
                    if (error != null){
                        return
                    }

                    for (dc : DocumentChange in value?.documentChanges!!){
                        messageModelList.add(dc.document.toObject(MessageModel::class.java))
                    }

                    mutableData.postValue(messageModelList)
                }

            })

        return mutableData

    }



    suspend fun sendMessage(messageText: String){
        var messageModel = MessageModel(messageText, auth.currentUser?.uid!!, Timestamp.now())

        db.collection(FireStoreCollections.CHATS).document(senderRoom!!).collection(FireStoreCollections.MESSAGES).add(messageModel).await()
        db.collection(FireStoreCollections.CHATS).document(recieverRoom!!).collection(FireStoreCollections.MESSAGES).add(messageModel).await()
    }


    suspend fun sendImageMessage(listPhothos: List<String>){


        listPhothos.forEach { url ->
            val messageModel = MessageModel(null, auth.currentUser?.uid!!, Timestamp.now(), url)

            db.collection(FireStoreCollections.CHATS).document(senderRoom!!).collection(FireStoreCollections.MESSAGES).add(messageModel).await()
            db.collection(FireStoreCollections.CHATS).document(recieverRoom!!).collection(FireStoreCollections.MESSAGES).add(messageModel).await()

        }

    }


    fun uriPhotoToUrl(listPhotho:List<Uri>):List<String> =  listPhotho.map { it.toString() }


    suspend fun getUser(userId:String, result: (UserModel)->Unit){
        var user = db.collection(FireStoreCollections.USERS).document(userId).get().await().toObject(UserModel::class.java)

        if (user != null) {
            result.invoke(user)
        }

    }


    fun setRoom(receiverUid:String){
        senderRoom = receiverUid + auth.currentUser?.uid!!
        recieverRoom = auth.currentUser?.uid!! + receiverUid

    }


    fun getCurrentUserId():String = auth.currentUser?.uid!!




}