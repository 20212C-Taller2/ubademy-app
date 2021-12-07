package com.fiuba.ubademy.network.model

data class EditCourseRequest(
    val title : String,
    val description : String,
    val type : String,
    val creator : String,
    val location : String?,
    val media : List<String>
)