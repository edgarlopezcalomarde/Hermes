package com.edloca.hermes.data.models

data class UserModel(
    val username:String? = "",
    var email:String? = "",
    var uid:String? = "",
    var friendTag:String? = "",
    var friendList:MutableList<String>? = mutableListOf(),
    var imgUrl:String? = "",
    var userstate:String?= "offline" ) {


    constructor():this(null,null,null,null,null,null,"offline")

}