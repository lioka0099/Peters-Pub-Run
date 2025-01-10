package com.example.collisions_game.models

data class Player(
    val name: String,
    val score: Int,
    val latitude: Double? = null,
    val longitude: Double? = null
)
