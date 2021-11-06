package com.fiuba.ubademy.main.courses

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("title")
fun TextView.setTitle(item: Course) {
    text = item.title
}

@BindingAdapter("description")
fun TextView.setDescription(item: Course) {
    text = item.description
}