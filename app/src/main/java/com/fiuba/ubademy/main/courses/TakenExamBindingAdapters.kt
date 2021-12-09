package com.fiuba.ubademy.main.courses

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.fiuba.ubademy.R

@BindingAdapter("title")
fun TextView.setTitle(item: TakenExam) {
    text = item.title
}

@BindingAdapter("status")
fun TextView.setStatus(item: TakenExam) {
    text = if (item.examSubmission == null)
        context.getString(R.string.pending)
    else if (item.examSubmission.examReview == null)
        context.getString(R.string.taken)
    else
        context.getString(R.string.reviewed)
}

@BindingAdapter("grade")
fun TextView.setGrade(item: TakenExam) {
    val grade = item.examSubmission?.examReview?.grade
    text = grade?.toString() ?: ""
}