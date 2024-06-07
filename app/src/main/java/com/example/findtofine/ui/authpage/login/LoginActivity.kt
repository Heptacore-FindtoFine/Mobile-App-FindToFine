package com.example.findtofine.ui.authpage.login

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import com.example.findtofine.R
import com.example.findtofine.databinding.ActivityLoginBinding
import com.example.findtofine.databinding.NotifCustomLoginBinding
import com.example.findtofine.ui.authpage.forgotpw.ForgotPwActivity
import com.example.findtofine.ui.authpage.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSignUp.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPwActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogIn.setOnClickListener {
            showCustomNotif()
        }
    }

    private fun showCustomNotif(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = NotifCustomLoginBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.btnTravel.setOnClickListener {
            // Handle button click
            dialog.dismiss()
        }

        dialog.show()
    }
}