package com.fiuba.ubademy

import com.fiuba.ubademy.network.model.UpdateFcmTokenRequest
import com.fiuba.ubademy.utils.api
import com.fiuba.ubademy.utils.getSharedPreferencesData
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.*
import timber.log.Timber

class UbademyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Timber.i("Refreshed FirebaseMessaging token: $token")

        val userId = baseContext.getSharedPreferencesData().id
        if (userId.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).run {
                launch {
                    try {
                        baseContext.api().updateFcmToken(userId, UpdateFcmTokenRequest(token))
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        }
    }
}