package com.example.collisions_game.activities

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.collisions_game.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimation(binding.splashLOTTIELottie)
    }

    private fun startAnimation(lottieAnimationView: LottieAnimationView) {
        lottieAnimationView.resumeAnimation()
        lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator) {
                //
            }

            override fun onAnimationEnd(animation: Animator) {
                transactionToMenu()
            }

            override fun onAnimationCancel(animation: Animator) {
                //
            }

            override fun onAnimationRepeat(animation: Animator) {
                //
            }

        })
    }

    private fun transactionToMenu(){
        startActivity(Intent(this, MenuActivity::class.java))
        finish()
    }
}