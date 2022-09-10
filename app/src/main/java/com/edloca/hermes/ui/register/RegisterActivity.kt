package com.edloca.hermes.ui.register

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.edloca.hermes.R
import com.edloca.hermes.data.models.UserModel
import com.edloca.hermes.databinding.ActivityRegisterBinding
import com.edloca.hermes.ui.login.LoginActivity
import com.edloca.hermes.utils.UiState
import com.edloca.hermes.utils.createLoadDialog
import com.edloca.hermes.utils.isValidEmail
import com.edloca.hermes.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private  val registerViewModel: RegisterViewModel by viewModels()

    private  lateinit var loadingDialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = this.createLoadDialog()


        registerViewModel.registerResponse.observe(this, Observer { state ->
            when(state){
                is UiState.Loading -> {
                    loadingDialog.show()
                }
                is UiState.Failure -> {
                    toast(state.error)
                    loadingDialog.dismiss()
                }
                is UiState.Success -> {
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                    toast(state.data)
                    loadingDialog.dismiss()

                }
            }
        })


        binding.btnRegister.setOnClickListener {
            if (validation()){
                registerViewModel.register(
                    email = binding.etEmail.text.toString(),
                    password = binding.etPassword.text.toString(),
                    user = setUser()
                )
            }
        }

        binding.tvAccount.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }


    }

    fun setUser():UserModel =
        UserModel(
             username = binding.etUser.text.toString(),
             email = binding.etEmail.text.toString(),
        )

    fun validation():Boolean{
        var isValid = true

        if (binding.etUser.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_username))
        }
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

        if (binding.etRepPassword.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_password))
        }else{
            if (binding.etPassword.text.toString() != binding.etRepPassword.text.toString()){
                isValid = false
                toast(getString(R.string.no_match_passwords))
            }


        }
        return isValid
    }



}