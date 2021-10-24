package com.fiuba.ubademy.utils

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.fiuba.ubademy.BuildConfig
import com.fiuba.ubademy.UbademyApplication
import com.fiuba.ubademy.network.UbademyApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

const val name = "ubademy_shared_preferences"

const val pref_id_key = "PREFERENCE_USER_ID"
const val pref_first_name_key = "PREFERENCE_USER_FIRST_NAME"
const val pref_last_name_key = "PREFERENCE_USER_LAST_NAME"
const val pref_place_id_key = "PREFERENCE_USER_PLACE_ID_EMAIL"
const val pref_place_name_key = "PREFERENCE_USER_PLACE_NAME_EMAIL"
const val pref_email_key = "PREFERENCE_USER_EMAIL"
const val pref_token_key = "PREFERENCE_USER_TOKEN"

fun AndroidViewModel.setSharedPreferencesData(sharedPreferencesData: SharedPreferencesData) {
    val sharedPreferences = getApplication<UbademyApplication>().getSharedPreferences(name, Context.MODE_PRIVATE)
    with (sharedPreferences.edit()) {
        putString(pref_id_key, sharedPreferencesData.id)
        putString(pref_first_name_key, sharedPreferencesData.firstName)
        putString(pref_last_name_key, sharedPreferencesData.lastName)
        putString(pref_place_id_key, sharedPreferencesData.placeId)
        putString(pref_place_name_key, sharedPreferencesData.placeName)
        putString(pref_email_key, sharedPreferencesData.email)
        putString(pref_token_key, sharedPreferencesData.token)
        apply()
    }
}

fun AndroidViewModel.getSharedPreferencesData() : SharedPreferencesData {
    val sharedPreferences = getApplication<UbademyApplication>().getSharedPreferences(name, Context.MODE_PRIVATE)
    return SharedPreferencesData(
        id = sharedPreferences.getString(pref_id_key, "")!!,
        firstName = sharedPreferences.getString(pref_first_name_key, "")!!,
        lastName = sharedPreferences.getString(pref_last_name_key, "")!!,
        placeId = sharedPreferences.getString(pref_place_id_key, "")!!,
        placeName = sharedPreferences.getString(pref_place_name_key, "")!!,
        email = sharedPreferences.getString(pref_email_key, "")!!,
        token = sharedPreferences.getString(pref_token_key, "")!!
    )
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

fun AndroidViewModel.api() : UbademyApiService {
    val sharedPreferencesData = getSharedPreferencesData()

    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${sharedPreferencesData.token}")
                .build()
            chain.proceed(newRequest)
        }.build()

    val retrofit = Retrofit.Builder()
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BuildConfig.BASE_URL)
        .build()

    return retrofit.create(UbademyApiService::class.java)
}