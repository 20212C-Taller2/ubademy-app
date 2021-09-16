package com.fiuba.ubademy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val splashScreenAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_anim)

        val splashScreenView: ImageView = findViewById(R.id.splashScreenLogo)
        splashScreenView.startAnimation(splashScreenAnimation)

        val mainIntent = Intent(this, MainActivity::class.java)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(mainIntent)
            finish()
        }, 2000)

    }
}