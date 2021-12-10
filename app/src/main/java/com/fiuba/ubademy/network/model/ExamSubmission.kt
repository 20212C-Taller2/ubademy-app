package com.fiuba.ubademy.network.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExamSubmission(
    val id: Int,
    @Json(name = "exam_id")
    val examId: Int,
    @Json(name = "student")
    val studentId: String,
    @Json(name = "answers")
    val answeredQuestions: List<AnsweredQuestion>,
    @Json(name = "review")
    val examReview: ExamReview?
) : Parcelable
