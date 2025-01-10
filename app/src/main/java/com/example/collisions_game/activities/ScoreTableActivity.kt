package com.example.collisions_game.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.collisions_game.interfaces.OnPlayerClickListener
import com.example.collisions_game.R
import com.example.collisions_game.databinding.ActivityScoreTableBinding
import com.example.collisions_game.fragments.MapFragment
import com.example.collisions_game.fragments.ScoreFragment
import com.example.collisions_game.models.Player

class ScoreTableActivity : AppCompatActivity(), OnPlayerClickListener {

    private lateinit var highScoreFragment: ScoreFragment
    private lateinit var mapFragment: MapFragment
    private lateinit var binding: ActivityScoreTableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreTableBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }


    private fun initViews() {
        highScoreFragment = ScoreFragment()
        mapFragment = MapFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.score_FRAME_score_table, highScoreFragment)
            .add(R.id.score_FRAME_map, mapFragment)
            .commit()
    }

    override fun onPlayerClicked(player: Player){
        if(player.latitude != null && player.longitude != null){
            mapFragment.zoomToLocation(player.latitude, player.longitude)
        }
    }

}