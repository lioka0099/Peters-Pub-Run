package com.example.collisions_game.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.collisions_game.interfaces.OnPlayerClickListener
import com.example.collisions_game.databinding.ScoreItemBinding
import com.example.collisions_game.models.Player

class PlayersAdapter (
    private val players: List<Player>
) : RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder>() {

    var itemCallback: OnPlayerClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = ScoreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        // Retrieve the current Player
        val player = players[position]
        holder.bind(player, position)
    }

    override fun getItemCount(): Int {
        // Number of items in the list
        return players.size
    }

    // Inner class that represents each score item
    inner class PlayerViewHolder(private val binding: ScoreItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Binds a Player object to the UI elements in score_item.xml
        fun bind(player: Player, position: Int) {
            // Leaderboard rank (position starts at 0, so +1)
            binding.scoreTXTPlayerNumber.text = (position + 1).toString()
            // Player name
            binding.scoreTXTPlayerName.text = player.name
            // Player score
            binding.scoreTXTPlayerScore.text = "${player.score}xp"

             binding.root.setOnClickListener {
                 itemCallback?.onPlayerClicked(player)
             }
        }
    }
}
