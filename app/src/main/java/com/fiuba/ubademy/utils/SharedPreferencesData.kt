package com.fiuba.ubademy.utils

const val name = "ubademy_shared_preferences"

const val pref_id_key = "P_ID"
const val pref_first_name_key = "P_FIRST_NAME"
const val pref_last_name_key = "P_LAST_NAME"
const val pref_place_id_key = "P_PLACE_ID"
const val pref_place_name_key = "P_PLACE_NAME"
const val pref_email_key = "P_EMAIL"
const val pref_token_key = "P_TOKEN"
const val pref_logged_in_with_google_key = "P_LOGGED_IN_WITH_GOOGLE"
const val pref_display_name_key = "P_DISPLAY_NAME"
const val pref_picture_key = "P_PICTURE"
const val pref_interests_key = "P_INTERESTS"

data class SharedPreferencesData(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val placeId: String?,
    val placeName: String?,
    val email: String,
    val token: String,
    val loggedInWithGoogle: Boolean,
    val displayName: String?,
    val picture: String?,
    val interests: Set<String>
)

fun SharedPreferencesData.getDisplayName() : String {
    return if (loggedInWithGoogle) displayName!! else "$firstName $lastName"
}