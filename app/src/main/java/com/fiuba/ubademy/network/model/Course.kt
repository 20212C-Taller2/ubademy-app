package com.fiuba.ubademy.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Course(
    val id: Int,
    val title : String,
    val description : String,
    val type: String,
    val creator: String,
    val media: List<String>,
    val location: String?
) : Parcelable
{
    val mediaSorted
        get() = media.sorted()
}