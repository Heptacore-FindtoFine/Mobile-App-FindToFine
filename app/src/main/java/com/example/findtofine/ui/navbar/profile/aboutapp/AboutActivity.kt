package com.example.findtofine.ui.navbar.profile.aboutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.findtofine.R
import com.example.findtofine.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}