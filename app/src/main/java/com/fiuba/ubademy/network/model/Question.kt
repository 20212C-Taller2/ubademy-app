package com.fiuba.ubademy.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    val id: Int,
    val number: Int,
    val text: String
) : Parcelable