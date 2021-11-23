package com.fiuba.ubademy.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GoogleData(
    val displayName: String,
    val picture: String
) : Parcelable