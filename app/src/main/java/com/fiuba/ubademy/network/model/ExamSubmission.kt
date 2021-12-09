package com.fiuba.ubademy.network.model

data class ExamSubmission(
    val id: Int,
    val examId: Int,
    val studentId: String,
    val answers: List<Answer>,
    val examReview: ExamReview?
)
