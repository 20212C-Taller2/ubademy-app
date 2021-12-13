package com.fiuba.ubademy.network.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExamReview(
    @Json(name = "user")
    val reviewerId: String,
    val feedback: String?,
    val grade: Int
) : Parcelable
