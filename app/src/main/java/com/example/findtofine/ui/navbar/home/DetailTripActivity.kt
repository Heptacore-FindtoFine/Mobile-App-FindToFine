package com.example.findtofine.ui.navbar.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.findtofine.R
import com.example.findtofine.databinding.ActivityDetailTripBinding

class DetailTripActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTripBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Detail Trip"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }
}