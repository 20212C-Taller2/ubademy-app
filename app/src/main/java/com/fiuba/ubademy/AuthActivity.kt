package com.fiuba.ubademy

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        when (intent.extras?.get(InvalidSessionReason::class.simpleName) as InvalidSessionReason?) {
            InvalidSessionReason.TOKEN_EXPIRED -> Toast.makeText(this, R.string.expired_session, Toast.LENGTH_LONG).show()
            InvalidSessionReason.USER_BLOCKED -> Toast.makeText(this, R.string.user_blocked, Toast.LENGTH_LONG).show()
        }
    }
}