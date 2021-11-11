package com.fiuba.ubademy.utils

import android.content.Context

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
        displayName = sharedPreferences.getString(pref_display_name_key, null)
    )
}