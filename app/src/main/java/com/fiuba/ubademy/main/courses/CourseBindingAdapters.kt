package com.fiuba.ubademy.main.courses

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.fiuba.ubademy.network.model.Course

@BindingAdapter("title")
fun TextView.setTitle(item: Course) {
    text = item.title
}

@BindingAdapter("description")
fun TextView.setDescription(item: Course) {
    text = item.description
}

@BindingAdapter("type")
fun TextView.setType(item: Course) {
    text = context.getString(resources.getIdentifier(item.type, "string", context.packageName))
}

@BindingAdapter("subscription")
fun TextView.setSubscription(item: Course) {
    text = context.getString(resources.getIdentifier(item.subscription, "string", context.packageName))
}