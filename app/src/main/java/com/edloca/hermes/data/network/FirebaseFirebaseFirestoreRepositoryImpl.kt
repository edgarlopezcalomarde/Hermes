package com.edloca.hermes.data.network

import android.net.Uri
import com.edloca.hermes.data.models.UserModel
import com.edloca.hermes.utils.FireStoreCollections.USERS
import com.edloca.hermes.utils.FireStoreDocumentField.EMAIL
import com.edloca.hermes.utils.FireStoreDocumentField.USERNAME
import com.edloca.hermes.utils.FireStoreField.FRIENDLIST
import com.edloca.hermes.utils.FireStoreField.STATE
import com.edloca.hermes.utils.FirebaseStorageConstants.PROFILE_IMAGE
import com.edloca.hermes.utils.UiState
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

class FirebaseFirebaseFirestoreRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
): FirebaseFirestoreRepository{

    override suspend fun addFriend(friendTag: String, result: (UiState<String>) -> Unit) {
        searchFriendTag(friendTag){
            db.collection(USERS).document(it).update(FRIENDLIST, FieldValue.arrayUnion(auth.currentUser!!.uid))
            db.collection(USERS).document(auth.currentUser!!.uid).update(FRIENDLIST, FieldValue.arrayUnion(it))
        }

    }



    override fun getUsersList(result: (UiState<List<UserModel>>) -> Unit) {
        db.collection(USERS).get()
            .addOnSuccessListener {
                result.invoke(UiState.Success(it.toObjects(UserModel::class.java).toList()))
            }
            .addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))
            }
    }

    override suspend fun getFriendList(result: (List<UserModel>) -> Unit) {
        val friendListString = getFriendsListString()

        val mutableListFriends: MutableList<UserModel> = mutableListOf()

        if (friendListString.isNotEmpty()){
           for (friendId in  friendListString){

               mutableListFriends.add(
                   db.collection(USERS).document(friendId).get().await().toObject(UserModel::class.java)!!
               )

           }

            result.invoke(mutableListFriends)

        }


    }

    override suspend fun getUser(result: (UserModel)->Unit){
        var user = db.collection(USERS).document(auth.currentUser?.uid.toString()).get().await().toObject(UserModel::class.java)

        if (user != null) {
            result.invoke(user)
        }

    }


    override suspend fun updateProfile(
        fields:Map<String, String>,
        result: (UiState<String>) -> Unit
    ) {

        for (field in fields){
            updateField(field.key, field.value)
        }

    }

    override  fun changeUserState( userState: String) {
        try {
            db.collection(USERS).document(auth.currentUser?.uid!!).update(STATE, userState)
        }catch (e:Exception){

        }

    }


    suspend fun getFriendsListString():List<String>{

        return try {
            var user = db.collection(USERS).document(auth.currentUser!!.uid).get().await().toObject(UserModel::class.java)

            user?.friendList ?: emptyList()
        }catch (e:Exception){
            emptyList()
        }

    }


    suspend fun searchFriendTag(friendTag:String, result: (String) -> Unit){
        var users =db.collection(USERS).get().await().toObjects(UserModel::class.java)
        users.forEach { userModel -> if (  userModel.friendTag == friendTag) result.invoke(userModel.uid!!) }
    }


    suspend fun updateField(field: String, fieldContent: String){
        db.collection(USERS).document(auth.currentUser?.uid.toString()).update(field, fieldContent).await()
    }


    override suspend fun uploadImgProfile(fileUri: Uri, onResult: (UiState<Uri>) -> Unit) {
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                storage.reference
                    .child(auth.currentUser?.uid!!)
                    .child(PROFILE_IMAGE)
                    .child(auth.currentUser?.uid!!)
                    .putFile(fileUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            updateImgUrl(uri.toString())
            onResult.invoke(UiState.Success(uri))
        } catch (e: FirebaseException){
            onResult.invoke(UiState.Failure(e.message))
        }catch (e: Exception){
            onResult.invoke(UiState.Failure(e.message))
        }
    }


    /*
    val reference = storage.reference
                .child(auth.currentUser?.uid!!)
                .child(PROFILE_IMAGE)
                .child(auth.currentUser?.uid!!)


                reference.putFile(fileUri).addOnSuccessListener {
                    reference.downloadUrl.addOnSuccessListener {
                        onResult.invoke(UiState.Success(it))
                    }
                }
    */


    suspend fun updateImgUrl(fieldContent: String){
        db.collection(USERS).document(auth.currentUser?.uid.toString())
            .update(PROFILE_IMAGE, fieldContent).await()
    }

    override suspend fun updateUsername(fieldContent: String, result: (UiState<String>) -> Unit) {
        db.collection(USERS).document(auth.currentUser?.uid.toString())
            .update(USERNAME, fieldContent).await()

    }

    override suspend fun updateEmail(fieldContent: String, result: (UiState<String>) -> Unit) {
        if (auth.currentUser != null){
            auth.currentUser?.updateEmail(fieldContent)?.addOnSuccessListener {
                db.collection(USERS).document(auth.currentUser?.uid.toString())
                    .update(EMAIL, fieldContent)
                    .addOnSuccessListener {
                        result.invoke(UiState.Success("Email update succesfully"))
                    }
                    .addOnFailureListener {
                        result.invoke(UiState.Failure(it.localizedMessage))
                    }


            }?.addOnFailureListener {
                result.invoke(UiState.Failure(it.localizedMessage))

            }

        }

    }

    override fun sendEmailForResetPass(result: (UiState<String>) -> Unit) {
        if (auth.currentUser !=null){
            auth.sendPasswordResetEmail(auth.currentUser?.email!!)
                .addOnSuccessListener {
                    result.invoke(UiState.Success("Email for reset pass sended"))
                }
                .addOnFailureListener {
                    result.invoke(UiState.Failure(it.localizedMessage))
                }
        }
    }






}