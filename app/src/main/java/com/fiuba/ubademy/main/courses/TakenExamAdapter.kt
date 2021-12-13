package com.fiuba.ubademy.main.courses

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

class TakenExamAdapter : ListAdapter<TakenExam, TakenExamViewHolder>(TakenExamDiffCallback()) {

    var onTakenExamItemClick: ((TakenExam) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TakenExamViewHolder {
        return TakenExamViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TakenExamViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onTakenExamItemClick?.invoke(item)
        }
        holder.bind(item)
    }
}