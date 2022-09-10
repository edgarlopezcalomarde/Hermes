package com.edloca.hermes.ui.chatlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edloca.hermes.ui.chat.ChatActivity
import com.edloca.hermes.ui.profile.ProfileActivity
import com.edloca.hermes.R
import com.edloca.hermes.databinding.ActivityChatListBinding
import com.edloca.hermes.ui.chat.RecyclerMessage
import com.edloca.hermes.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatListActivity : AppCompatActivity(), RecyclerFriends.ClickListerner {

    private lateinit var binding: ActivityChatListBinding
    private lateinit var rvChatList:RecyclerView
    private lateinit var friendsAdapter: RecyclerFriends

    private val chatListViewModel: ChatListViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.modern_blue)

        chatListViewModel.getFriendsList()

        friendsAdapter = RecyclerFriends(this ,this)
        rvChatList = binding.rvChatList
        rvChatList.layoutManager = LinearLayoutManager(this)
        rvChatList.adapter = friendsAdapter


        binding.apply {
            btnSettings.setOnClickListener {
                startActivity(Intent(this@ChatListActivity, ProfileActivity::class.java))
                finish()
            }

            btnnLogOut.setOnClickListener {
                chatListViewModel.logOut()
                startActivity(Intent(this@ChatListActivity, LoginActivity::class.java))
                finish()
            }

            btnAddUser.setOnClickListener {
               if (!binding.etSearchUser.text.isNullOrEmpty()){
                   chatListViewModel.addFriend(binding.etSearchUser.text.toString())
               }
            }
        }



        chatListViewModel.friendsList.observe(this, Observer {
            if (it.isNotEmpty()) friendsAdapter.setlistData(it)
        })



        chatListViewModel.isLoading.observe(this, Observer {
            if (it){
                binding.shimmerRv.visibility = View.VISIBLE
                binding.rvChatList.visibility = View.GONE
            }else{
                binding.shimmerRv.visibility = View.GONE
                binding.rvChatList.visibility = View.VISIBLE
            }
        })


    }


    override fun onItemClick(userFriendId: String) {
        var intento = Intent(this@ChatListActivity, ChatActivity::class.java)
        intento.putExtra("reciverid", userFriendId)
        startActivity(intento)
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        chatListViewModel.changeState("offline")

    }


    override fun onStart() {
        super.onStart()
        chatListViewModel.changeState("online")
    }





}