package com.fiuba.ubademy.utils

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.fiuba.ubademy.UbademyApplication

const val name = "ubademy_shared_preferences"

const val pref_id_key = "PREFERENCE_USER_ID"
const val pref_first_name_key = "PREFERENCE_USER_FIRST_NAME"
const val pref_last_name_key = "PREFERENCE_USER_LAST_NAME"
const val pref_email_key = "PREFERENCE_USER_EMAIL"
const val pref_token_key = "PREFERENCE_USER_TOKEN"

fun AndroidViewModel.addSharedPreferencesData(sharedPreferencesData: SharedPreferencesData) {
    val sharedPreferences = getApplication<UbademyApplication>().getSharedPreferences(name, Context.MODE_PRIVATE)
    with (sharedPreferences.edit()) {
        putString(pref_id_key, sharedPreferencesData.id)
        putString(pref_first_name_key, sharedPreferencesData.firstName)
        putString(pref_last_name_key, sharedPreferencesData.lastName)
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
        email = sharedPreferences.getString(pref_email_key, "")!!,
        token = sharedPreferences.getString(pref_token_key, "")!!
    )
}