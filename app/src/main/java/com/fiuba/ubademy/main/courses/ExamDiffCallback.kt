package com.fiuba.ubademy.main.courses

import androidx.recyclerview.widget.DiffUtil
import com.fiuba.ubademy.network.model.Exam

class ExamDiffCallback : DiffUtil.ItemCallback<Exam>() {
    override fun areItemsTheSame(oldItem: Exam, newItem: Exam): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Exam, newItem: Exam): Boolean {
        return oldItem == newItem
    }
}