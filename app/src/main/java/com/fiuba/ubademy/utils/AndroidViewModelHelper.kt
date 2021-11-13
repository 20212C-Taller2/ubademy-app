package com.fiuba.ubademy.utils

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.fiuba.ubademy.BuildConfig
import com.fiuba.ubademy.UbademyApplication
import com.fiuba.ubademy.network.UbademyCoursesApiService
import com.fiuba.ubademy.network.UbademyUsersApiService
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

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
        putBoolean(pref_logged_in_with_google_key, sharedPreferencesData.loggedInWithGoogle)
        putString(pref_display_name_key, sharedPreferencesData.displayName)
        putString(pref_picture_key, sharedPreferencesData.picture)
        apply()
    }
}

fun AndroidViewModel.getSharedPreferencesData() : SharedPreferencesData {
    return getApplication<UbademyApplication>().applicationContext.getSharedPreferencesData()
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

fun AndroidViewModel.usersApi() : UbademyUsersApiService {
    val retrofit = Retrofit.Builder()
        .client(getDefaultClient())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BuildConfig.USERS_BASE_URL)
        .build()

    return retrofit.create(UbademyUsersApiService::class.java)
}

fun AndroidViewModel.coursesApi() : UbademyCoursesApiService {
    val retrofit = Retrofit.Builder()
        .client(getDefaultClient())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BuildConfig.COURSES_BASE_URL)
        .build()

    return retrofit.create(UbademyCoursesApiService::class.java)
}

private fun AndroidViewModel.getDefaultClient() : OkHttpClient {
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

suspend fun AndroidViewModel.getPlaceById(placeId: String) : Place? {
    val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)
    val request = FetchPlaceRequest.newInstance(placeId, placeFields)
    val placesClient = Places.createClient(getApplication())

    return try {
        val fetchPlaceResponse = placesClient.fetchPlace(request).await()
        fetchPlaceResponse.place
    } catch (exception: Exception) {
        Timber.e(exception)
        null
    }
}