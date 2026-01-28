package com.example.memorygame.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.R
import com.example.memorygame.databinding.ItemCardBinding

/**
 * RecyclerView Adapter for displaying the memory cards.
 * @param context The application context.
 * @param cards The list of MemoryCard objects to display.
 * @param cardClickListener A lambda function invoked when a card is tapped.
 */
class CardAdapter(
    private val context: Context,
    private val cards: MutableList<com.example.memorygame.model.MemoryCard>,
    private val cardClickListener: (Int) -> Unit
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    // The drawable resource ID for the card's back side (face-down)
    private val cardBackRes = R.drawable.card_back

    /**
     * ViewHolder for the individual card item.
     */
    inner class ViewHolder(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.cardImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = cards[position]

        // Set the image based on card state (face-up/face-down)
        if (card.isFaceUp || card.isMatched) {
            // Show the front icon if face-up or matched
            holder.imageView.setImageResource(card.imageRes)
        } else {
            // Show the card back if face-down
            holder.imageView.setImageResource(cardBackRes)
        }

        // Set up the click listener for the card
        holder.imageView.setOnClickListener {
            // Only handle click if the card is not matched and not currently face-up
            if (!card.isMatched) {
                cardClickListener(position)
            }
        }
    }

    override fun getItemCount() = cards.size
}