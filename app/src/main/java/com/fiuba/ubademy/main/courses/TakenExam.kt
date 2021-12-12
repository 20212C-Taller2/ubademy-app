package com.fiuba.ubademy.main.courses

import android.os.Parcelable
import com.fiuba.ubademy.network.model.ExamSubmission
import com.fiuba.ubademy.network.model.Question
import kotlinx.parcelize.Parcelize

@Parcelize
data class TakenExam(
    val examId: Int,
    val title: String,
    val questions: List<Question>,
    val examSubmission: ExamSubmission?
) : Parcelable