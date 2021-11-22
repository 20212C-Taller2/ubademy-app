package com.fiuba.ubademy.network.model

data class UnenrollCourseRequest(
    val userId: String,
    val courseId: Int
)