package com.fiuba.ubademy.utils

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.fiuba.ubademy.UbademyApplication
import com.fiuba.ubademy.network.UbademyApiService
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import kotlinx.coroutines.tasks.await
import timber.log.Timber

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
        putStringSet(pref_interests_key, sharedPreferencesData.interests)
        apply()
    }
}

fun AndroidViewModel.getSharedPreferencesData() : SharedPreferencesData {
    return getApplication<UbademyApplication>().applicationContext.getSharedPreferencesData()
}

fun AndroidViewModel.api() : UbademyApiService {
    return getApplication<UbademyApplication>().applicationContext.api()
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