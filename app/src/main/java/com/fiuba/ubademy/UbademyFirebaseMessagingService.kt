package com.fiuba.ubademy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.fiuba.ubademy.network.model.GetUserResponse
import com.fiuba.ubademy.network.model.UpdateFcmTokenRequest
import com.fiuba.ubademy.utils.api
import com.fiuba.ubademy.utils.getSharedPreferencesChat
import com.fiuba.ubademy.utils.getSharedPreferencesData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random

class UbademyFirebaseMessagingService : FirebaseMessagingService() {
    var random: Random = Random

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

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<GetUserResponse> = moshi.adapter(GetUserResponse::class.java)
        val userFrom = jsonAdapter.fromJson(remoteMessage.data["userFrom"]!!)
        val userTo = jsonAdapter.fromJson(remoteMessage.data["userTo"]!!)

        val (currentChatUserId, otherChatUserId) = getSharedPreferencesChat()
        if (userTo?.id == currentChatUserId && userFrom?.id == otherChatUserId)
            return

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("userFrom", userFrom)
        intent.putExtra("userTo", userTo)
        intent.putExtra("message", remoteMessage.notification?.body)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = "ubademy_fcm_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_ubademy_notification)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, "Ubademy FCM notifications", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(random.nextInt(), notificationBuilder.build())
    }
}