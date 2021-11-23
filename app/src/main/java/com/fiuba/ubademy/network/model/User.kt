package com.fiuba.ubademy.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val placeId: String?,
    val email: String,
    val interests: Set<String>
) : Parcelable