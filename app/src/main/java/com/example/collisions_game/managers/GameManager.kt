package com.example.collisions_game.managers

import android.content.Context
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.collisions_game.R
import com.example.collisions_game.databinding.ActivityMainBinding
import com.example.collisions_game.interfaces.GameOverListener
import com.example.collisions_game.utilities.SignalManager
import com.example.collisions_game.utilities.SingleSoundPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameManager(
    private val context: Context,
    private val binding: ActivityMainBinding,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val playerManager: PlayerManager,
    private var delay: Long,
    private val gameOverListener: GameOverListener
) {

    private val signalManager = SignalManager.getInstance()
    private val rows = 8
    private val cols = 5
    private var peterRow: Int = rows - 2
    private var peterCol: Int = cols / 2
    private var timerJob : Job? = null
    private var timerOn: Boolean = false

     fun initializeGrid(){
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                // Add chickens to grid
                val chicken = AppCompatImageView(context).apply {
                    tag = "chicken_${row}_${col}"
                    setImageResource(R.drawable.chicken)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    visibility = View.INVISIBLE
                }

                val chickenParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(row, 1f)
                    columnSpec = GridLayout.spec(col, 1f)
                    width = 0
                    height = 0
                }

                chicken.layoutParams = chickenParams
                binding.gameGrid.addView(chicken)

                // Add beer to grid
                val beer = AppCompatImageView(context).apply {
                    tag = "beer_${row}_${col}"
                    setImageResource(R.drawable.beer)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    visibility = View.INVISIBLE
                }

                val beerParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(row, 1f)
                    columnSpec = GridLayout.spec(col, 1f)
                    width = 0
                    height = 0
                }

                beer.layoutParams = beerParams
                binding.gameGrid.addView(beer)

            }
        }

        // Add Peter Grifin to grid
        for (col in 0 until cols) {
            val peterImageView = AppCompatImageView(context).apply {
                tag = "peter_${col}_${peterRow}"
                setImageResource(R.drawable.peter_grifin)
                scaleType = ImageView.ScaleType.FIT_CENTER
                visibility = if (col == peterCol) View.VISIBLE else View.INVISIBLE
            }

            val params = GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(peterRow, 1f)
                columnSpec = GridLayout.spec(col, 1f)
                width = 0
                height = 0

            }
            peterImageView.layoutParams = params
            binding.gameGrid.addView(peterImageView)
        }
    }

   private fun startSpawningObjects() {
        if (!timerOn) {
            timerOn = true
            timerJob = lifecycleScope.launch {
                val objectsList = listOf("chicken", "beer")
                while (timerOn) {
                    val randomObject = objectsList.random()
                    trySpawnObjects(randomObject)
                    delay(delay)
                }
            }
        }
    }

    private suspend fun trySpawnObjects(objectType: String) {
        val spawnProbability: Double = if (objectType == "beer") {
            0.5
        } else {
            0.8
        }

        val randomValue = Math.random()

        if (randomValue < spawnProbability) {
            val randomCol = (0 until cols).random()
            spawnObjects(0, randomCol, objectType)
        }
    }

    private suspend fun spawnObjects(row: Int, col: Int, objectType: String) {
        binding.gameGrid.findViewWithTag<AppCompatImageView>("${objectType}_${row}_${col}").visibility =
            View.VISIBLE
        delay(delay)
        lifecycleScope.launch {
            moveChicken(row, col, objectType)
        }
    }

    private suspend fun moveChicken(startRow: Int, col: Int, objectType: String) {
        var currentRow = startRow
        while (currentRow < rows - 1 && timerOn) {
            val currentTag = "${objectType}_${currentRow}_${col}"
            val nextTag = "${objectType}_${currentRow + 1}_${col}"

            val currentView = binding.gameGrid.findViewWithTag<AppCompatImageView>(currentTag)
            currentView?.visibility = View.INVISIBLE

            if (peterRow == currentRow && peterCol == col) {
                handleCollisions(col, objectType)
                currentView?.visibility = View.INVISIBLE
                break
            }

            val nextView = binding.gameGrid.findViewWithTag<AppCompatImageView>(nextTag)
            nextView?.visibility = View.VISIBLE

            delay(delay)
            currentRow++
        }

        if (currentRow == rows - 1) {
            val lastTag = "${objectType}_${rows - 1}_${col}"
            val lastView = binding.gameGrid.findViewWithTag<AppCompatImageView>(lastTag)
            lastView?.visibility = View.INVISIBLE
        }
    }

    fun updatePeterPosition(newCol: Int, newRow: Int = peterRow) {
        peterCol = newCol
        peterRow = newRow
    }

    private fun handleCollisions(col: Int, objectType: String) {
        if (peterCol == col) {
            val collisions = playerManager.getCollisions()
            val mainHeartsContainer = playerManager.getHearts()
            val ssp = SingleSoundPlayer(context)

            if (objectType == "beer") {
                // Beer Collision → Just increase score and continue
                signalManager.toast("Drink Up!")
                playerManager.increaseScore()
                ssp.playSound(R.raw.peter_laugh)
                signalManager.vibrate()
            } else if (objectType == "chicken") {
                // Chicken Collision → Lose a heart
                signalManager.toast("A chick beat you!")
                mainHeartsContainer[collisions].visibility = View.INVISIBLE
                playerManager.increaseCollisions()
                ssp.playSound(R.raw.peter_ahhhhh)
                signalManager.vibrate()

                // Game Over only when all hearts are gone after chicken collision
                if (collisions == mainHeartsContainer.size - 1) {
                    pauseGame()
                    gameOverListener.onGameOver()
                }
            }
        }
    }

    fun updateDelay(newDelay: Long) {
        delay = newDelay
    }

    fun pauseGame() {
        timerOn = false
        timerJob?.cancel()
        clearGrid()
    }

    fun resumeGame(){
        startSpawningObjects()
    }


    private fun clearGrid() {
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                // Check for chicken obstacles
                val chickenTag = "chicken_${row}_${col}"
                binding.gameGrid.findViewWithTag<AppCompatImageView>(chickenTag)?.visibility = View.INVISIBLE

                // Check for beer obstacles
                val beerTag = "beer_${row}_${col}"
                binding.gameGrid.findViewWithTag<AppCompatImageView>(beerTag)?.visibility = View.INVISIBLE
            }
        }
    }

    fun getCols(): Int {
        return cols
    }

    fun getPeterCol(): Int {
        return peterCol
    }

    fun getPeterRow(): Int {
        return peterRow
    }

}