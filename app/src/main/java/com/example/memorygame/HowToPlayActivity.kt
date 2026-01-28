package com.example.memorygame


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.memorygame.databinding.ActivityHowToPlayBinding

class HowToPlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHowToPlayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHowToPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable the Up button (back arrow) in the ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "How to Play"
    }

    // Handle Up button press (same as pressing the system back button)
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}