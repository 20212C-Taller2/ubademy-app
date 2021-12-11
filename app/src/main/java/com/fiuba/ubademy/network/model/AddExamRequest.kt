package com.fiuba.ubademy.network.model

data class AddExamRequest(
    val title : String,
    val published: Boolean,
    val questions: List<Question>
)