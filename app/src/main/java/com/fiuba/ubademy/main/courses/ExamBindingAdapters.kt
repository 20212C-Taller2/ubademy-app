package com.fiuba.ubademy.main.courses

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.fiuba.ubademy.network.model.Exam

@BindingAdapter("title")
fun TextView.setTitle(item: Exam) {
    text = item.title
}