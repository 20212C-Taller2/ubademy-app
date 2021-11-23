package com.fiuba.ubademy.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetUserResponse(
    val user: User,
    val googleData: GoogleData?
) : Parcelable {
val displayName
    get() = googleData?.displayName ?: "${user.firstName} ${user.lastName}"
}
