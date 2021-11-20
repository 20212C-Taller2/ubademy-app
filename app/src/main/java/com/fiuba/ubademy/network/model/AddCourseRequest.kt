package com.fiuba.ubademy.network.model

data class AddCourseRequest(
    val title : String,
    val description : String,
    val type : String,
    val exams : Int = 1,
    val subscription : String = "free",
    val creator : String = "me",
    val location : String = "here",
    val media : List<String>
)