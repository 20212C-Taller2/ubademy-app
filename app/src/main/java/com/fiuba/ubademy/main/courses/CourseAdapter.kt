package com.fiuba.ubademy.main.courses

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

class CourseAdapter : ListAdapter<Course, CourseViewHolder>(CourseDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        return CourseViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}