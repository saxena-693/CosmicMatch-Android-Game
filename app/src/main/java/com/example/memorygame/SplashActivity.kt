package com.example.memorygame

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.memorygame.databinding.ActivitySplashBinding

/**
 * First screen shown: displays the game title/logo for a short duration.
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val SPLASH_DELAY_MS = 2500L // 2.5 seconds delay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Use Handler to delay the transition to the main menu
        Handler(Looper.getMainLooper()).postDelayed({
            // Start the MainMenuActivity
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)

            // Close the splash screen activity
            finish()
        }, SPLASH_DELAY_MS)
    }
}