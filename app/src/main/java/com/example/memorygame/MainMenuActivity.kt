package com.example.memorygame

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.memorygame.databinding.ActivityMainMenuBinding

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set listeners for menu buttons
        binding.btnPlay.setOnClickListener {
            startActivity(Intent(this, LevelSelectActivity::class.java))
        }

        binding.btnHowToPlay.setOnClickListener {
            startActivity(Intent(this, HowToPlayActivity::class.java))
        }

        binding.btnExit.setOnClickListener {
            finishAffinity() // Close all activities and exit the app
        }
    }
}