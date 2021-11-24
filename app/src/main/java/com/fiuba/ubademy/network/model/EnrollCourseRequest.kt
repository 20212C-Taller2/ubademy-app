package com.fiuba.ubademy.network.model

data class EnrollCourseRequest(
    val userId: String,
    val courseId: Int
)