package com.example.findtofine.ui.welcomepage.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.findtofine.databinding.ActivitySplashScreenBinding
import com.example.findtofine.ui.welcomepage.WelcomeActivity
import com.example.findtofine.ui.welcomepage.pages.Page1Activity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    private val splashTimeOut: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed({
            startActivity(Intent(this,WelcomeActivity::class.java))
            finish()
        }, splashTimeOut)

    }
}