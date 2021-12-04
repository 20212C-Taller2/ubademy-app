package com.fiuba.ubademy.utils

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fiuba.ubademy.R
import java.text.DecimalFormat

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(RequestOptions
                .circleCropTransform()
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .error(R.drawable.ic_baseline_broken_image_24))
            .into(imgView)
    }
}

@BindingAdapter("imageUri")
fun bindImage(imgView: ImageView, imgUri: Uri?) {
    imgUri?.let {
        Glide.with(imgView.context)
            .load(it)
            .apply(RequestOptions()
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_broken_image_24))
            .into(imgView)
    }
}

@BindingAdapter("balance")
fun TextView.setBalance(balance: Double) {
    val formatter = DecimalFormat("#,##0.00")
    text = formatter.format(balance)
}

@BindingAdapter("price")
fun TextView.setPrice(price: Double) {
    val formatter = DecimalFormat("#,##0.00")
    text = formatter.format(price)
}