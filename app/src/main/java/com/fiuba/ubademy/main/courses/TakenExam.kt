package com.fiuba.ubademy.main.courses

import com.fiuba.ubademy.network.model.ExamSubmission
import com.fiuba.ubademy.network.model.Question

data class TakenExam(
    val examId: Int,
    val title: String,
    val questions: List<Question>,
    val examSubmission: ExamSubmission?
)