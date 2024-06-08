package com.example.findtofine.ui.navbar.profile.editprofile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.findtofine.R
import com.example.findtofine.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnUpdateProfile.setOnClickListener {
            finish()
        }
    }
}