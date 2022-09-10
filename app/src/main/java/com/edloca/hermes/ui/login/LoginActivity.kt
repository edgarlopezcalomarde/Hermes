package com.edloca.hermes.ui.login

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.edloca.hermes.R
import com.edloca.hermes.ui.chatlist.ChatListActivity
import com.edloca.hermes.databinding.ActivityLoginBinding
import com.edloca.hermes.ui.register.RegisterActivity
import com.edloca.hermes.utils.UiState
import com.edloca.hermes.utils.createLoadDialog
import com.edloca.hermes.utils.isValidEmail
import com.edloca.hermes.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private  lateinit var loadingDialog: Dialog

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = this.createLoadDialog()


        loginViewModel.loginResponse.observe(this, Observer { state ->
            when(state){
                is UiState.Loading -> {
                    loadingDialog.show()
                }
                is UiState.Failure -> {
                    toast(state.error)
                    loadingDialog.dismiss()
                }
                is UiState.Success -> {
                    startActivity(Intent(this@LoginActivity, ChatListActivity::class.java))
                    finish()
                    toast(state.data)
                    loadingDialog.dismiss()
                }
            }

        })


        binding.btnLogin.setOnClickListener {
            if (validation()){
                loginViewModel.login(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                )
            }
        }

        binding.tvNoAccount.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            finish()
        }


    }


    fun validation(): Boolean {
        var isValid = true

        if (binding.etEmail.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_email))
        }else{
            if (!binding.etEmail.text.toString().isValidEmail()){
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }
        if (binding.etPassword.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_password))
        }else{
            if (binding.etPassword.text.toString().length < 8){
                isValid = false
                toast(getString(R.string.invalid_password))
            }
        }
        return isValid
    }


    public override fun onStart() {
        super.onStart()
        loginViewModel.getSession { user ->
            if (user != null){
                startActivity(Intent(this@LoginActivity, ChatListActivity::class.java))
                finish()
            }
        }
    }




}