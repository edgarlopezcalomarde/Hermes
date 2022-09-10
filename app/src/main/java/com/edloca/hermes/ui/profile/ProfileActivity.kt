package com.edloca.hermes.ui.profile

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.edloca.hermes.R
import com.edloca.hermes.databinding.ActivityProfileBinding
import com.edloca.hermes.ui.chatlist.ChatListActivity
import com.edloca.hermes.ui.login.LoginActivity
import com.edloca.hermes.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private  lateinit var loadingDialog: Dialog
    private var selectedImg: Uri?=null

    private val profileViewModel: ProfileViewModel by viewModels()

    private var responseLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode== AppCompatActivity.RESULT_OK){
            selectedImg=it.data?.data
            if (selectedImg != null) profileViewModel.updateProfilePhoto(selectedImg)
       }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        profileViewModel.getUserInfo()
        loadingDialog = this.createLoadDialog()


        profileViewModel.getUserInfo()

        profileViewModel.userModelInfo.observe(this, Observer {

            binding.etEmail.setText(it?.email.toString())
            binding.etUser.setText(it?.username.toString())
            binding.tvFriendTag.setText(it.friendTag.toString())

            Glide.with(this@ProfileActivity)
                .load(it?.imgUrl.toString())
                .circleCrop()
                .placeholder(R.drawable.account_circle)
                .into(binding.userProfilePicture)
        })



        binding.btnResetPasword.setOnClickListener {
            profileViewModel.resetPass()
        }



        binding.editEmail.setOnClickListener { showEmailMenu() }
        binding.cancelEditEmail.setOnClickListener { hideEmailMenu() }
        binding.acceptEditEmail.setOnClickListener {
            profileViewModel.updateEmail(binding.etEmail.text.toString())
            hideEmailMenu()

        }


        binding.editUsername.setOnClickListener { showUsernameMenu() }
        binding.cancelEditUser.setOnClickListener { hideUsernameMenu() }
        binding.acceptEditUser.setOnClickListener {
            profileViewModel.updateUsername(binding.etUser.text.toString())
            hideUsernameMenu()
        }


        binding.userProfilePicture.setOnClickListener { updatePhoto() }


        profileViewModel.userPhoto.observe(this, Observer { state ->

            when(state){
                is UiState.Loading -> { loadingDialog.show() }
                is UiState.Failure -> {
                    toast(state.error)
                    loadingDialog.dismiss()

                }
                is UiState.Success -> {
                    Glide.with(this@ProfileActivity)
                        .load(state.data)
                        .circleCrop()
                        .placeholder(R.drawable.account_circle)
                        .into(binding.userProfilePicture)
                    loadingDialog.dismiss()

                }
            }

        })

        profileViewModel.userEmail.observe(this, Observer { state ->

            when(state){
                is UiState.Loading -> { loadingDialog.show() }

                is UiState.Failure -> {
                    toast(state.error)
                    loadingDialog.dismiss()

                }
                is UiState.Success -> {toast(state.data)}
            }

        })


        profileViewModel.userName.observe(this, Observer { state ->
            when(state){
                is UiState.Loading -> { loadingDialog.show() }

                is UiState.Failure -> {
                    toast(state.error)
                    loadingDialog.dismiss()

                }
                is UiState.Success -> {toast(state.data)}
            }
        })


        profileViewModel.userEmailSend.observe(this, Observer { state ->
            when(state){
                is UiState.Loading -> { loadingDialog.show() }

                is UiState.Failure -> {
                    toast(state.error)
                    loadingDialog.dismiss()
                }
                is UiState.Success -> {toast(state.data)}
            }
        })


    }



    private fun updatePhoto() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        responseLauncher.launch(intent)
    }


    override fun onBackPressed() {
        startActivity(Intent(this@ProfileActivity, ChatListActivity::class.java))
        finish()
    }


    private fun hideEmailMenu(){
        binding.etEmail.isFocusable = true
        binding.etEmail.isFocusableInTouchMode = true
        binding.etEmail.inputType =  InputType.TYPE_NULL

        binding.editEmail.show()
        binding.cancelEditEmail.hide()
        binding.acceptEditEmail.hide()

        binding.root.hideKeyboard()
    }

    private fun showEmailMenu(){
        binding.etEmail.isFocusable = true
        binding.etEmail.isFocusableInTouchMode = true
        binding.etEmail.inputType = InputType.TYPE_CLASS_TEXT

        binding.editEmail.hide()
        binding.cancelEditEmail.show()
        binding.acceptEditEmail.show()
    }

    private fun hideUsernameMenu(){
        binding.etUser.isFocusable = true
        binding.etUser.isFocusableInTouchMode = true
        binding.etUser.inputType =  InputType.TYPE_NULL


        binding.editUsername.show()
        binding.cancelEditUser.hide()
        binding.acceptEditUser.hide()
        binding.root.hideKeyboard()
    }

    private fun showUsernameMenu(){
        binding.etUser.isFocusable = true
        binding.etUser.isFocusableInTouchMode = true
        binding.etUser.inputType = InputType.TYPE_CLASS_TEXT

        binding.editUsername.hide()
        binding.cancelEditUser.show()
        binding.acceptEditUser.show()
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}