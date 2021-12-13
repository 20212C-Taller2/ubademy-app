package com.fiuba.ubademy.network.model

data class AddCourseRequest(
    val title : String,
    val description : String,
    val type : String,
    val subscription : String,
    val creator : String,
    val location : String?,
    val media : List<String>
)