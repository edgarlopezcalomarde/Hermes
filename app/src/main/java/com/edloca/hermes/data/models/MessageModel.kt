package com.edloca.hermes.data.models


data class MessageModel (var message:String?, var senderId:String?, var timestamp:com.google.firebase.Timestamp?){
    constructor():this(null,null,null)

    var listPhotos:MutableList<String> = mutableListOf()
    var photo:String? = null

    constructor( message:String?,  senderId:String?,  timestamp:com.google.firebase.Timestamp?,  photho:String):this(message, senderId, timestamp){
        this.photo = photho
    }

    constructor( message:String?,  senderId:String?,  timestamp:com.google.firebase.Timestamp?,  listPhoto: MutableList<String>):this(message, senderId, timestamp){
        this.listPhotos = listPhotos
    }


}

