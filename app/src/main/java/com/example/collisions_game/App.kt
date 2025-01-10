package com.example.collisions_game

import android.app.Application
import com.example.collisions_game.utilities.SignalManager
import com.paz.prefy_lib.Prefy

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SignalManager.init(this)
        Prefy.init(this,true)
    }
}