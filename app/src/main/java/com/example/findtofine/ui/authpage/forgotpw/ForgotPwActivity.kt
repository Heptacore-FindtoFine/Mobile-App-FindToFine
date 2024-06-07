package com.example.findtofine.ui.authpage.forgotpw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.findtofine.databinding.ActivityForgotPwBinding
import com.example.findtofine.ui.authpage.verif.VerifActivity
import com.example.findtofine.ui.authpage.login.LoginActivity

class ForgotPwActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPwBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnContinue.setOnClickListener {
            val intent = Intent(this, VerifActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}