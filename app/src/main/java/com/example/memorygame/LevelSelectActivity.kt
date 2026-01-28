package com.example.memorygame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.memorygame.databinding.ActivityLevelSelectBinding

class LevelSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLevelSelectBinding

    // Keys for Intent extras
    companion object {
        const val EXTRA_ROWS = "EXTRA_ROWS"
        const val EXTRA_COLUMNS = "EXTRA_COLUMNS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLevelSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Select Level"

        // Set up click listeners for the level buttons
        binding.btnEasy.setOnClickListener { startGame(4, 4) }
        binding.btnMedium.setOnClickListener { startGame(4, 5) }
    }

    /**
     * Starts the GameActivity with the selected number of rows and columns.
     */
    private fun startGame(rows: Int, cols: Int) {
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra(EXTRA_ROWS, rows)
            putExtra(EXTRA_COLUMNS, cols)
        }
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}