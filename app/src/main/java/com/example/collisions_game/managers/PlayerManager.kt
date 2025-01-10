package com.example.collisions_game.managers

import android.content.Intent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.example.collisions_game.databinding.ActivityMainBinding
import com.example.collisions_game.models.PlayerStorage

class PlayerManager(private val binding: ActivityMainBinding) {
    private var score: Int = 0
    private var collisions: Int = 0
    private lateinit var hearts: Array<AppCompatImageView>

    fun initHearts(){
        hearts = arrayOf(binding.mainHEART1, binding.mainHEART2, binding.mainHEART3)
    }

    fun increaseScore() {
        score += 10
        binding.mainSCORE.text = "$score"
    }

    fun savePlayer(intent : Intent){
        val name = intent.getStringExtra("userName") ?: "Unknown"
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        PlayerStorage.addPlayer(name, score, latitude, longitude)
    }

    fun increaseCollisions(){
        collisions++
    }

    fun getCollisions(): Int {
        return collisions
    }

    fun getHearts(): Array<AppCompatImageView> {
        return hearts
    }
}