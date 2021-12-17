package com.fiuba.ubademy.main.courses

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.fiuba.ubademy.network.model.GetUserResponse

@BindingAdapter("displayName")
fun TextView.setDisplayName(item: GetUserResponse) {
    text = item.displayName
}