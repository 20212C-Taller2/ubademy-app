package com.fiuba.ubademy.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnsweredQuestion(
    val question: Question,
    val text: String
) : Parcelable