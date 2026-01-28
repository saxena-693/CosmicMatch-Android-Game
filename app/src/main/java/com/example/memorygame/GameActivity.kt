// GameActivity.kt
package com.example.memorygame

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memorygame.adapter.CardAdapter
import com.example.memorygame.databinding.ActivityGameBinding
import com.example.memorygame.model.MemoryCard

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var adapter: CardAdapter
    // This declaration is correct and fixes the previous error
    private lateinit var cards: MutableList<MemoryCard>
    private lateinit var countDownTimer: CountDownTimer

    // Game state variables
    private var numRows = 4
    private var numCols = 4
    private var totalPairs: Int = 0
    private var pairsFound: Int = 0
    private var moves: Int = 0
    private var hintsRemaining: Int = 3
    private var firstSelectedIndex: Int? = null
    private var isBusy: Boolean = false // Flag to prevent rapid tapping during mismatch check
    private var timeRemaining: Long = 0

    // Time constants
    private val TOTAL_TIME_SECONDS = 60L // Total time for the game (e.g., 60 seconds)
    private val TOTAL_TIME_MILLIS = TOTAL_TIME_SECONDS * 1000L
    private val DELAY_FLIP_BACK = 800L // Delay before flipping mismatched cards back

    // Keys for ResultActivity Intent
    companion object {
        const val EXTRA_IS_WIN = "EXTRA_IS_WIN"
        const val EXTRA_MOVES = "EXTRA_MOVES"
        const val EXTRA_TIME_REMAINING = "EXTRA_TIME_REMAINING"
        // Note: We use EXTRA_TIME_REMAINING to pass EITHER the remaining time (loss) or the time used (win).
    }

    // List of available card icons
    private val allIcons = listOf(
        R.drawable.card_rocket, R.drawable.card_star, R.drawable.card_planet,
        R.drawable.card_ufo, R.drawable.card_astronaut, R.drawable.card_comet,
        R.drawable.card_satellite, R.drawable.card_moon, R.drawable.card_galaxy,
        R.drawable.card_alien
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get level dimensions from Intent
        numRows = intent.getIntExtra(LevelSelectActivity.EXTRA_ROWS, 4)
        numCols = intent.getIntExtra(LevelSelectActivity.EXTRA_COLUMNS, 4)
        totalPairs = (numRows * numCols) / 2

        // Initialize game board and UI
        setupGame()
        setupTimer()
        setupListeners()
    }

    /**
     * Sets up the initial game state.
     *
     * This function initializes the game by:
     * 1. Generating a shuffled list of memory cards based on the selected grid size.
     * 2. Creating and setting up the `CardAdapter` for the `RecyclerView`.
     * 3. Configuring the `RecyclerView` with a `GridLayoutManager`.
     * 4. Updating the UI elements for moves, pairs, and hints.
     * 5. Setting the maximum value for the time progress bar.
     */
    private fun setupGame() {
        cards = createCards(numRows * numCols)
        adapter = CardAdapter(this, cards) { position ->
            handleCardClick(position)
        }

        binding.recyclerView.layoutManager = GridLayoutManager(this, numCols)
        binding.recyclerView.adapter = adapter

        // Initial UI update
        updateMovesAndPairsUI()
        binding.tvHints.text = hintsRemaining.toString()

        // Set progress bar max
        binding.progressBarTime.max = TOTAL_TIME_MILLIS.toInt()
    }

    /**
     * Generates and shuffles the list of MemoryCard objects for the game.
     */
    private fun createCards(totalCards: Int): MutableList<MemoryCard> {
        val numPairs = totalCards / 2
        // Select the necessary number of unique icons
        val chosenIcons = allIcons.shuffled().take(numPairs)

        // Create the list of cards by duplicating and assigning an ID
        val cardList = mutableListOf<MemoryCard>()
        for (i in 0 until numPairs) {
            // Each pair gets a unique ID (i) and the same icon
            cardList.add(MemoryCard(id = i, imageRes = chosenIcons[i]))
            cardList.add(MemoryCard(id = i, imageRes = chosenIcons[i]))
        }

        // Shuffle the cards to randomize their positions
        return cardList.apply { shuffle() }
    }

    /**
     * Handles the logic when a card is tapped.
     */
    private fun handleCardClick(position: Int) {
        // Prevent interaction if the game is busy (checking a mismatch)
        if (isBusy || cards[position].isMatched || cards[position].isFaceUp) {
            return
        }

        // Flip the card up and notify the adapter to redraw
        cards[position].isFaceUp = true
        adapter.notifyItemChanged(position)

        if (firstSelectedIndex == null) {
            // 1st card selected
            firstSelectedIndex = position
        } else {
            // 2nd card selected
            isBusy = true // Temporarily lock interaction

            // Increment move count
            moves++
            updateMovesAndPairsUI()

            val firstCard = cards[firstSelectedIndex!!]
            val secondCard = cards[position]

            if (firstCard.id == secondCard.id) {
                // Match found!
                pairsFound++
                firstCard.isMatched = true
                secondCard.isMatched = true
                updateMovesAndPairsUI()
                resetSelectionAndUnlock()

                // Check for win condition
                if (pairsFound == totalPairs) {
                    gameEnd(isWin = true)
                }
            } else {
                // Mismatch, flip both back after a delay
                Handler(Looper.getMainLooper()).postDelayed({
                    firstCard.isFaceUp = false
                    secondCard.isFaceUp = false
                    adapter.notifyItemChanged(firstSelectedIndex!!)
                    adapter.notifyItemChanged(position)
                    resetSelectionAndUnlock()
                }, DELAY_FLIP_BACK)
            }
        }
    }

    /**
     * Resets the first selected card index and unlocks the interaction flag.
     */
    private fun resetSelectionAndUnlock() {
        firstSelectedIndex = null
        isBusy = false
    }

    /**
     * Updates the moves and pairs TextViews.
     */
    private fun updateMovesAndPairsUI() {
        binding.tvMoves.text = "Moves: $moves"
        binding.tvPairs.text = "Pairs: $pairsFound / $totalPairs"
    }

    /**
     * Sets up the CountDownTimer for the game time limit.
     */
    private fun setupTimer() {
        countDownTimer = object : CountDownTimer(TOTAL_TIME_MILLIS, 100) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                val seconds = (millisUntilFinished / 1000).toInt()
                binding.tvTime.text = "${seconds}s"
                // Update progress bar
                binding.progressBarTime.progress = millisUntilFinished.toInt()
            }

            override fun onFinish() {
                // Time is up, player loses
                gameEnd(isWin = false)
            }
        }
        countDownTimer.start()
    }

    /**
     * Sets up listeners for the back button and hint button.
     */
    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            // Stop the timer and return to the level select screen
            countDownTimer.cancel()
            finish()
        }

        binding.btnHint.setOnClickListener {
            useHint()
        }
    }

    /**
     * Implements the hint system logic.
     */
    private fun useHint() {
        if (isBusy || hintsRemaining <= 0) {
            Toast.makeText(this, if (isBusy) "Wait a moment" else "No hints left", Toast.LENGTH_SHORT).show()
            return
        }

        // Find an unmatched pair
        val unmatchedCards = cards.filter { !it.isMatched }
        val matchedPair = unmatchedCards.groupBy { it.id }
            .filter { it.value.size >= 2 } // Check size >= 2 for a valid unmatched pair
            .values.firstOrNull()

        if (matchedPair != null) {
            hintsRemaining--
            binding.tvHints.text = hintsRemaining.toString()

            // Find indices of the two cards in the main list
            val index1 = cards.indexOf(matchedPair[0])
            val index2 = cards.indexOf(matchedPair[1])

            // Temporarily reveal both cards
            cards[index1].isFaceUp = true
            cards[index2].isFaceUp = true
            adapter.notifyItemChanged(index1)
            adapter.notifyItemChanged(index2)

            isBusy = true // Lock during hint display

            // Flip them back after 1 second
            Handler(Looper.getMainLooper()).postDelayed({
                cards[index1].isFaceUp = false
                cards[index2].isFaceUp = false
                adapter.notifyItemChanged(index1)
                adapter.notifyItemChanged(index2)
                isBusy = false // Unlock
            }, 1000)
        } else {
            Toast.makeText(this, "All pairs matched!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Ends the game and navigates to the ResultActivity.
     * Passes time taken on win, or time remaining (0) on loss.
     */
    private fun gameEnd(isWin: Boolean) {
        countDownTimer.cancel()

        // Calculate time to pass: Time Used (on win) or 0 (on loss/time up)
        val timeData = if (isWin) TOTAL_TIME_MILLIS - timeRemaining else 0L

        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(EXTRA_IS_WIN, isWin)
            putExtra(EXTRA_MOVES, moves)
            // Passes time used (if win) or 0 (if lose)
            putExtra(EXTRA_TIME_REMAINING, timeData)
            putExtra(LevelSelectActivity.EXTRA_ROWS, numRows)
            putExtra(LevelSelectActivity.EXTRA_COLUMNS, numCols)
        }
        startActivity(intent)
        finish() // Close the GameActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel() // Ensure the timer is stopped when the activity is destroyed
    }
}