package com.example.findtofine.ui.authpage.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.findtofine.R
import com.example.findtofine.databinding.ActivityRegisterBinding
import com.example.findtofine.ui.authpage.forgotpw.ForgotPwActivity
import com.example.findtofine.ui.authpage.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, ForgotPwActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}