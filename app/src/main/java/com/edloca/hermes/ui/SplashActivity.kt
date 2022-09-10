package com.edloca.hermes.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import com.edloca.hermes.R
import com.edloca.hermes.databinding.ActivitySplashBinding
import com.edloca.hermes.ui.login.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lanzarLogin()
    }

    private fun lanzarLogin() {

        object : CountDownTimer(800, 100){
            override fun onTick(p0: Long) {}

            override fun onFinish() {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }

        }.start()


    }


}