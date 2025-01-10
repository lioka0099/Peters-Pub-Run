package com.example.collisions_game.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.collisions_game.interfaces.OnPlayerClickListener
import com.example.collisions_game.R
import com.example.collisions_game.adapters.PlayersAdapter
import com.example.collisions_game.models.PlayerStorage

class ScoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_score, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.main_RV_list)

        val players = PlayerStorage.getPlayerList()
        players.sortByDescending { it.score }
        val adapter = PlayersAdapter(players)
        adapter.itemCallback = activity as? OnPlayerClickListener

        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

    }
}