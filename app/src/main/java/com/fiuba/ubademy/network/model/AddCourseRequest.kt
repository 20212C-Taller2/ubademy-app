package com.fiuba.ubademy.network.model

data class AddCourseRequest(
    val title : String,
    val description : String,
    val exams : Int = 1,
    val subscription : String = "free",
    val type : String = "WEB_DEV",
    val creator : String = "me",
    val location : String = "here"
)