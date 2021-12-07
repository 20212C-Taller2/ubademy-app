package com.fiuba.ubademy.network.model

data class AddExamRequest(
    val title : String,
    val questions: List<Question>
)