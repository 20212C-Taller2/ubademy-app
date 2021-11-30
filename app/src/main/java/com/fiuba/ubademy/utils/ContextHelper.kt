package com.fiuba.ubademy.utils

import android.content.Context
import com.fiuba.ubademy.BuildConfig
import com.fiuba.ubademy.network.UbademyApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

fun Context.getSharedPreferencesData() : SharedPreferencesData {
    val sharedPreferences = getSharedPreferences(name, Context.MODE_PRIVATE)
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

private fun Context.getDefaultClient() : OkHttpClient {
    val sharedPreferencesData = getSharedPreferencesData()

    return OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${sharedPreferencesData.token}")
                .build()
            chain.proceed(newRequest)
        }.build()
}