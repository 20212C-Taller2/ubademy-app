package com.fiuba.ubademy.network.model

data class UpsertExamRequest(
    val title : String,
    val published: Boolean,
    val questions: List<Question>
)