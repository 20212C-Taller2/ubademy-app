package com.fiuba.ubademy.network.model

import com.squareup.moshi.Json

data class Answer(
    @Json(name = "question_id")
    val questionId: Int,
    val text: String
)
