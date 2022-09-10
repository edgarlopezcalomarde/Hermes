package com.edloca.hermes.ui.chatlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edloca.hermes.R
import com.edloca.hermes.data.models.UserModel

class RecyclerFriends(
    var context:Context,

    var itemClickListener: ClickListerner
    ): RecyclerView.Adapter<RecyclerFriends.FriendViewHolder>() {

    private var chatFriendList: List<UserModel> = listOf()


    fun setlistData(listData: List<UserModel>?) {
        if (listData != null) this.chatFriendList = listData
    }

    interface ClickListerner{
        fun onItemClick(userFriendId:String)
    }

    inner class FriendViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){


        var userProfile = itemView.findViewById<ImageView>(R.id.iv_user_profile)
        var userName = itemView.findViewById<TextView>(R.id.tv_user_name)
        var lastMessage = itemView.findViewById<TextView>(R.id.tv_last_message)



        fun bind(position: Int) {
            itemView.setOnClickListener {  itemClickListener.onItemClick(chatFriendList[position].uid.toString())}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        var itemview = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false)
        return FriendViewHolder(itemview)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.userName.text = chatFriendList[position].username
        Glide.with(context)
            .load(chatFriendList[position].imgUrl)
            .circleCrop()
            .placeholder(R.drawable.account_circle)
            .into(holder.userProfile)

        holder.lastMessage.text =  chatFriendList[position].userstate
        
        holder.bind(position)
    }

    override fun getItemCount(): Int = chatFriendList.size

}