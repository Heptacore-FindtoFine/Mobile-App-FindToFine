package com.example.findtofine.ui.authpage.newpw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.findtofine.R
import com.example.findtofine.databinding.ActivityNewPwBinding
import com.example.findtofine.ui.authpage.login.LoginActivity
import com.example.findtofine.ui.authpage.verif.VerifActivity

class NewPwActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewPwBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, VerifActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnUpdatePassword.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}