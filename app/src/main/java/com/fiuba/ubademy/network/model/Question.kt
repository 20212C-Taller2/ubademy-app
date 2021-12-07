package com.fiuba.ubademy.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    val number: Int,
    val text: String
) : Parcelable