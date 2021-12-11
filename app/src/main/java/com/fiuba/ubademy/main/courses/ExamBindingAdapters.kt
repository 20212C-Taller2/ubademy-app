package com.fiuba.ubademy.main.courses

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.fiuba.ubademy.R
import com.fiuba.ubademy.network.model.Exam

@BindingAdapter("title")
fun TextView.setTitle(item: Exam) {
    text = item.title
}

@BindingAdapter("state")
fun TextView.setState(item: Exam) {
    text = if (item.published)
        context.getString(R.string.published)
    else
        context.getString(R.string.not_published)
}