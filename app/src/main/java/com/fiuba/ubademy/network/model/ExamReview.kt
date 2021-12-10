package com.fiuba.ubademy.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExamReview(
    val reviewerId: String,
    val grade: Int
) : Parcelable
