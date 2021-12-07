package com.fiuba.ubademy.main.courses

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.fiuba.ubademy.network.model.Exam

class ExamAdapter : ListAdapter<Exam, ExamViewHolder>(ExamDiffCallback()) {

    var onExamItemClick: ((Exam) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        return ExamViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onExamItemClick?.invoke(item)
        }
        holder.bind(item)
    }
}