package com.fiuba.ubademy.main.courses

import androidx.recyclerview.widget.DiffUtil
import com.fiuba.ubademy.network.model.Course

class CourseDiffCallback : DiffUtil.ItemCallback<Course>() {
    override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
        return oldItem == newItem
    }
}