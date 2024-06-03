package com.example.findtofine.ui.welcomepage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.findtofine.MainActivity
import com.example.findtofine.R
import com.example.findtofine.databinding.ActivityWelcomeBinding
import com.google.android.material.button.MaterialButton

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layouts = listOf(
            R.layout.activity_page1,
            R.layout.activity_page2,
            R.layout.activity_page3,
            R.layout.activity_page4
        )

        val adapter = WelcomeViewPager(layouts)
        binding.viewPager.adapter = adapter
        binding.indicator.setViewPager(binding.viewPager)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.btnPrev.visibility = if (position == 0) View.GONE else View.VISIBLE
                binding.btnNext.visibility = if (position == layouts.size - 1) View.GONE else View.VISIBLE
                binding.btnGetStarted.visibility = if (position == layouts.size - 1) View.VISIBLE else View.GONE

            }
        })

        binding.btnNext.setOnClickListener {
            val nextItem = binding.viewPager.currentItem +1
            if (nextItem < layouts.size){
                binding.viewPager.currentItem = nextItem
            }
        }

        binding.btnPrev.setOnClickListener {
            val prevItem = binding.viewPager.currentItem -1
            if (prevItem >= 0){
                binding.viewPager.currentItem = prevItem
            }
        }

        binding.btnGetStarted.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}