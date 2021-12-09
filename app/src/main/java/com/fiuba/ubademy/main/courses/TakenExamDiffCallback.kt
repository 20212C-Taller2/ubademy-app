package com.fiuba.ubademy.main.courses

import androidx.recyclerview.widget.DiffUtil

class TakenExamDiffCallback : DiffUtil.ItemCallback<TakenExam>() {
    override fun areItemsTheSame(oldItem: TakenExam, newItem: TakenExam): Boolean {
        return oldItem.examId == newItem.examId && oldItem.examSubmission?.id == newItem.examSubmission?.id
    }

    override fun areContentsTheSame(oldItem: TakenExam, newItem: TakenExam): Boolean {
        return oldItem == newItem
    }
}