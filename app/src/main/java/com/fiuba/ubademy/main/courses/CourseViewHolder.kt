package com.fiuba.ubademy.main.courses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fiuba.ubademy.databinding.ItemCourseBinding
import com.fiuba.ubademy.network.model.Course

class CourseViewHolder private constructor(val binding: ItemCourseBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Course) {
        binding.course = item
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): CourseViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemCourseBinding.inflate(layoutInflater, parent, false)
            return CourseViewHolder(binding)
        }
    }
}