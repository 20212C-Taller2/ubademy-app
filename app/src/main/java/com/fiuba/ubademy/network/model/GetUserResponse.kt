package com.fiuba.ubademy.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetUserResponse(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val placeId: String?,
    val email: String,
    val interests: Set<String>,
    val googleData: GoogleData?
) : Parcelable {
val displayName
    get() = googleData?.displayName ?: "$firstName $lastName"
}
