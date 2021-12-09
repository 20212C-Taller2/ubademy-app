package com.fiuba.ubademy.main.courses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fiuba.ubademy.databinding.ItemTakenExamBinding

class TakenExamViewHolder private constructor(val binding: ItemTakenExamBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: TakenExam) {
        binding.takenExam = item
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): TakenExamViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemTakenExamBinding.inflate(layoutInflater, parent, false)
            return TakenExamViewHolder(binding)
        }
    }
}