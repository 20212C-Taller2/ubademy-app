package com.fiuba.ubademy

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val splashScreenAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_anim)

        val splashScreenView: ImageView = findViewById(R.id.splashScreenLogo)
        splashScreenView.startAnimation(splashScreenAnimation)

        val authIntent = Intent(this, AuthActivity::class.java)

        val currentUser = Firebase.auth.currentUser
        val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
        // TODO: also check normal email/password

        Handler(Looper.getMainLooper()).postDelayed({
            if (currentUser != null) {
                // TODO: go to main
                startActivity(authIntent)
                finish()
            } else {
                startActivity(authIntent)
                finish()
            }
        }, 2000)
    }
}