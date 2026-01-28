package com.example.memorygame

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.memorygame.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    // Data received from GameActivity
    private var isWin = false
    private var moves = 0
    private var timeMillis = 0L
    private var rows = 0
    private var cols = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from Intent
        intent.apply {
            isWin = getBooleanExtra(GameActivity.EXTRA_IS_WIN, false)
            moves = getIntExtra(GameActivity.EXTRA_MOVES, 0)
            timeMillis = getLongExtra(GameActivity.EXTRA_TIME_REMAINING, 0)
            rows = getIntExtra(LevelSelectActivity.EXTRA_ROWS, 4)
            cols = getIntExtra(LevelSelectActivity.EXTRA_COLUMNS, 4)
        }

        // Display results
        updateResultUI()

        // Set up button listeners
        binding.btnPlayAgain.setOnClickListener {
            // Restart the same level
            val intent = Intent(this, GameActivity::class.java).apply {
                putExtra(LevelSelectActivity.EXTRA_ROWS, rows)
                putExtra(LevelSelectActivity.EXTRA_COLUMNS, cols)
            }
            startActivity(intent)
            finish()
        }

        binding.btnMainMenu.setOnClickListener {
            // Go back to MainMenuActivity
            val intent = Intent(this, MainMenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }
    }

    private fun updateResultUI() {
        val timeInSeconds = timeMillis / 1000

        // Set the main result message
        if (isWin) {
            binding.tvResultTitle.text = "Level ${rows}x$cols Cleared!"
            binding.tvTimeStat.text = "Time Taken: $timeInSeconds seconds"
        } else {
            binding.tvResultTitle.text = "Time Up! Try Again."
            binding.tvTimeStat.text = "Time Remaining: 0 seconds"
        }

        // Display stats
        binding.tvMovesStat.text = "Moves Used: $moves"
    }
}