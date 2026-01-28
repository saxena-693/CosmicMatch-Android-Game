package com.example.memorygame
// MemoryCard.kt

/**
 * Data class representing a single memory card.
 * @param id The unique identifier for the pair (e.g., 1 for all rockets).
 * @param imageRes The drawable resource ID for the card's front icon.
 * @param isFaceUp True if the card is currently visible (face-up).
 * @param isMatched True if the card has been successfully matched.
 */
data class MemoryCard(
    val id: Int,
    val imageRes: Int,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)