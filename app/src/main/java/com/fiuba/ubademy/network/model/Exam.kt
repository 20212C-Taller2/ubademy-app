package com.fiuba.ubademy.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exam(
    val id: Int,
    val title: String,
    val questions: List<Question>
) : Parcelable