package com.example.findtofine.ui.mainfeature.mf1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.findtofine.R
import com.example.findtofine.databinding.ActivityMainFeature1Binding
import com.example.findtofine.ui.mainfeature.mf2.MainFeature2Activity

class MainFeature1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityMainFeature1Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainFeature1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnNext.setOnClickListener {
            val intent = Intent(this,MainFeature2Activity::class.java)
            startActivity(intent)
            finish()
        }
    }
}