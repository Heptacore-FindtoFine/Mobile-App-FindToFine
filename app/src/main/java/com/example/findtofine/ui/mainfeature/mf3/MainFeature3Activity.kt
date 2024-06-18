package com.example.findtofine.ui.mainfeature.mf3

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.findtofine.R
import com.example.findtofine.databinding.ActivityMainFeature3Binding

class MainFeature3Activity : AppCompatActivity() {
    private lateinit var binding: ActivityMainFeature3Binding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainFeature3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        val startDate = intent.getStringExtra("startDate")
        val finishDate = intent.getStringExtra("finishDate")
        val location = intent.getStringExtra("location")
        val description = intent.getStringExtra("description")
        val imageUriString = intent.getStringExtra("imageUri")
        val items = intent.getStringArrayListExtra("items")

        binding.tvDate.text = "Date : ${startDate} - ${finishDate}"
        binding.tvLocation.text = "Location : ${location}"
        binding.tvDeskripsi.text = "Description : ${description}"

        imageUriString?.let { uriString ->
            val imageUri = Uri.parse(uriString)
            Glide.with(this)
                .load(imageUri)
                .into(binding.gambarThumnail)
        }

        items?.let {
            val adapter = ItemAdapter(it)
            binding.recyclerView.adapter = adapter
        }
    }
}