package com.fiuba.ubademy

import com.google.firebase.messaging.FirebaseMessagingService
import timber.log.Timber

class UbademyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Timber.i("Refreshed FirebaseMessaging token: $token")

        //TODO: sendRegistrationToServer(token)
    }
}