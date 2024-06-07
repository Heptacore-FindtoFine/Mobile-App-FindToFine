package com.example.findtofine.ui.authpage.verif

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.findtofine.databinding.ActivityVerifBinding
import com.example.findtofine.ui.authpage.forgotpw.ForgotPwActivity
import com.example.findtofine.ui.authpage.newpw.NewPwActivity

class VerifActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, ForgotPwActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnVerif.setOnClickListener {
            val intent = Intent(this,NewPwActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}