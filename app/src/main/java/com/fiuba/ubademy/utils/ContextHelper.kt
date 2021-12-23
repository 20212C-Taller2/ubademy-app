package com.fiuba.ubademy.utils

import android.content.Context
import android.content.Intent
import com.fiuba.ubademy.AuthActivity
import com.fiuba.ubademy.BuildConfig
import com.fiuba.ubademy.InvalidSessionReason
import com.fiuba.ubademy.network.UbademyApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

fun Context.getSharedPreferencesData() : SharedPreferencesData {
    val sharedPreferences = getSharedPreferences(ubademy_user_shared_preferences, Context.MODE_PRIVATE)
    return SharedPreferencesData(
        id = sharedPreferences.getString(pref_id_key, "")!!,
        firstName = sharedPreferences.getString(pref_first_name_key, null),
        lastName = sharedPreferences.getString(pref_last_name_key, null),
        placeId = sharedPreferences.getString(pref_place_id_key, null),
        placeName = sharedPreferences.getString(pref_place_name_key, null),
        email = sharedPreferences.getString(pref_email_key, "")!!,
        token = sharedPreferences.getString(pref_token_key, "")!!,
        loggedInWithGoogle = sharedPreferences.getBoolean(pref_logged_in_with_google_key, false),
        displayName = sharedPreferences.getString(pref_display_name_key, null),
        picture = sharedPreferences.getString(pref_picture_key, null),
        interests = sharedPreferences.getStringSet(pref_interests_key, setOf())!!
    )
}

fun Context.clearSharedPreferencesData() {
    deleteSharedPreferences(ubademy_user_shared_preferences)
}

fun Context.setSharedPreferencesChat(currentUser: String, otherUser: String) {
    val sharedPreferences = applicationContext.getSharedPreferences(ubademy_chat_shared_preferences, Context.MODE_PRIVATE)
    with (sharedPreferences.edit()) {
        putString(pref_chat_current_user_key, currentUser)
        putString(pref_chat_other_user_key, otherUser)
        apply()
    }
}

fun Context.clearSharedPreferencesChat() {
    deleteSharedPreferences(ubademy_chat_shared_preferences)
}

fun Context.getSharedPreferencesChat() : Pair<String, String> {
    val sharedPreferences = getSharedPreferences(ubademy_chat_shared_preferences, Context.MODE_PRIVATE)
    return Pair(sharedPreferences.getString(pref_chat_current_user_key, "")!!, sharedPreferences.getString(pref_chat_other_user_key, "")!!)
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

fun Context.api() : UbademyApiService {
    val retrofit = getDefaultRetrofitBuilder()
        .baseUrl(BuildConfig.BASE_URL)
        .build()

    return retrofit.create(UbademyApiService::class.java)
}

private fun Context.getDefaultRetrofitBuilder() : Retrofit.Builder {
    return Retrofit.Builder()
        .client(getDefaultClient())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
}

private val freePaths = listOf("users/register", "users/login", "users/login/google", "courses/types")

private fun Context.getDefaultClient() : OkHttpClient {
    val sharedPreferencesData = getSharedPreferencesData()

    return OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val encodedPath = chain.request().url().encodedPath()
            val validSessionRequired = freePaths.none { path -> encodedPath.contains(path) }

            val newRequest: Request =
                if (validSessionRequired)
                    chain.request().newBuilder().addHeader("Authorization", "Bearer ${sharedPreferencesData.token}").build()
                else
                    chain.request()

            val response = chain.proceed(newRequest)

            if (validSessionRequired && response.code() == 401) {
                val bodyString = response.body()?.string()
                val intent = Intent(applicationContext, AuthActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                if (bodyString?.contains("expired", true) == true)
                    intent.putExtra(InvalidSessionReason::class.simpleName, InvalidSessionReason.TOKEN_EXPIRED)
                else if (bodyString?.contains("blocked", true) == true)
                    intent.putExtra(InvalidSessionReason::class.simpleName, InvalidSessionReason.USER_BLOCKED)
                applicationContext.startActivity(intent)
            }
            response
        }.build()
}