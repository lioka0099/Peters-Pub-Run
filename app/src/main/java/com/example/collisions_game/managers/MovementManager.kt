package com.example.collisions_game.managers

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.example.collisions_game.interfaces.TiltCallback
import com.example.collisions_game.databinding.ActivityMainBinding
import com.example.collisions_game.utilities.GameMode
import com.example.collisions_game.utilities.TiltDetector

class MovementManager (
    private val context: Context,
    private val binding: ActivityMainBinding,
    private val gameManager: GameManager,
    private val gameMode: GameMode,
    private var delay: Long
){
    private lateinit var tiltDetector: TiltDetector

    fun initTiltDetector() {
        if (gameMode == GameMode.SENSORS) {
            tiltDetector = TiltDetector(context, object : TiltCallback {
                override fun tiltX(left: Boolean) {
                    movePeter(left)
                }

                override fun tiltY(increaseSpeed: Boolean) {
                    if (increaseSpeed) {
                        delay = (delay - 100).coerceAtLeast(300L)
                    } else {
                        delay = (delay + 100).coerceAtMost(1000L)
                    }
                    gameManager.updateDelay(delay)
                }
            })
            tiltDetector.calibrate()
        }
    }

    fun movePeter(toLeft: Boolean) {
        val peterCol = gameManager.getPeterCol()
        val peterRow = gameManager.getPeterRow()
        val newCol = if (toLeft) peterCol - 1 else peterCol + 1

        if (newCol in 0 until gameManager.getCols()) {
            val currentTag = "peter_${peterCol}_${peterRow}"
            binding.gameGrid.findViewWithTag<AppCompatImageView>(currentTag)
                ?.visibility = View.INVISIBLE

            gameManager.updatePeterPosition(newCol)

            val newTag = "peter_${newCol}_${peterRow}"
            binding.gameGrid.findViewWithTag<AppCompatImageView>(newTag)
                ?.visibility = View.VISIBLE
        }
    }

    fun stopMovement() {
        tiltDetector.stop()
    }

    fun resumeMovement() {
        tiltDetector.start()
    }
}