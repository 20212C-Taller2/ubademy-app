package com.fiuba.ubademy.network.model

data class SubmitExamRequest(
    val student: String,
    val answers: List<Answer>
)
