package com.example.findtofine

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.findtofine.databinding.ActivityMainBinding
import com.example.findtofine.ui.mainfeature.mf1.MainFeature1Activity
import com.example.findtofine.ui.navbar.guide.GuideFragment
import com.example.findtofine.ui.navbar.history.HistoryFragment
import com.example.findtofine.ui.navbar.home.HomeFragment
import com.example.findtofine.ui.navbar.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var doubleBackToExitPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavigation

        binding.fab.setOnClickListener{
            val intent = Intent(this, MainFeature1Activity::class.java)
            startActivity(intent)
        }

        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val fragment = HomeFragment()
                    openFragment(fragment)
                    true
                }
                R.id.navigation_history -> {
                    val fragment = HistoryFragment()
                    openFragment(fragment)
                    true
                }
                R.id.navigation_guide -> {
                    val fragment = GuideFragment()
                    openFragment(fragment)
                    true
                }
                R.id.navigation_profile -> {
                    val fragment = ProfileFragment()
                    openFragment(fragment)
                    true
                }
                else -> false
            }
        }

        // Set default fragment
        navView.selectedItemId = R.id.navigation_home
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        if(doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        AlertDialog.Builder(this)
            .setMessage("Press back again to exit")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                finish()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                doubleBackToExitPressedOnce = false
            })
            .show()

        // Delay to reset the flag
        Handler().postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }
}