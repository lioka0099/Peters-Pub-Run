package com.example.collisions_game.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.collisions_game.databinding.ActivityMainBinding
import com.example.collisions_game.interfaces.GameOverListener
import com.example.collisions_game.managers.GameManager
import com.example.collisions_game.managers.MovementManager
import com.example.collisions_game.managers.PlayerManager
import com.example.collisions_game.utilities.GameMode

class MainActivity : AppCompatActivity(), GameOverListener {

    private lateinit var gameMode: GameMode
    private lateinit var binding: ActivityMainBinding
    private lateinit var playerManager: PlayerManager
    private lateinit var gameManager: GameManager
    private lateinit var movementManager: MovementManager

    private var delay: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        gameMode = intent.getSerializableExtra("gameMode") as GameMode
        delay = intent.getLongExtra("speed", 500L)

        playerManager = PlayerManager(binding)
        gameManager = GameManager(this, binding, lifecycleScope, playerManager, delay, this)
        movementManager = MovementManager(this, binding, gameManager, gameMode, delay)

        initUI()
        movementManager.initTiltDetector()
    }


    private fun initUI() {
        playerManager.initHearts()
        gameManager.initializeGrid()

        if (gameMode == GameMode.BUTTONS) {
            binding.mainLEFTARROWBTN.setOnClickListener { movementManager.movePeter(true) }
            binding.mainRIGHTARROWBTN.setOnClickListener { movementManager.movePeter(false) }
        } else {
            binding.mainLEFTARROWBTN.visibility = View.GONE
            binding.mainRIGHTARROWBTN.visibility = View.GONE
        }
        gameManager.resumeGame()
    }

    override fun onStop() {
        super.onStop()
        gameManager.pauseGame()

        if (gameMode == GameMode.SENSORS) {
            movementManager.stopMovement()
        }
    }

    override fun onResume() {
        super.onResume()

        gameManager.resumeGame()

        if (gameMode == GameMode.SENSORS) {
            movementManager.resumeMovement()
        }
    }

    override fun onGameOver() {
        //add Player to Prefy
        playerManager.savePlayer(intent)
        val intent = Intent(this, ScoreTableActivity::class.java)
        startActivity(intent)
        finish()
    }
}