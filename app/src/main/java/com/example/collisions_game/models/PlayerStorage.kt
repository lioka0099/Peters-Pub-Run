package com.example.collisions_game.models

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paz.prefy_lib.Prefy

object PlayerStorage {
    private val gson = Gson()

    fun getPlayerList(): MutableList<Player> {
        val json = Prefy.getInstance().getString("PLAYERS_LIST","[]")
        val type = object : TypeToken<MutableList<Player>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addPlayer(name:String, score:Int, latitude:Double, longitude:Double){
        val players = getPlayerList()
        players.add(Player(name,score,latitude,longitude))
        val updatedJson = gson.toJson(players)
        Prefy.getInstance().putString("PLAYERS_LIST",updatedJson)
    }
}