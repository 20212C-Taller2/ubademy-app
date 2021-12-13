package com.fiuba.ubademy.main.courses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fiuba.ubademy.databinding.ItemExamBinding
import com.fiuba.ubademy.network.model.Exam

class ExamViewHolder private constructor(val binding: ItemExamBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Exam) {
        binding.exam = item
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ExamViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemExamBinding.inflate(layoutInflater, parent, false)
            return ExamViewHolder(binding)
        }
    }
}