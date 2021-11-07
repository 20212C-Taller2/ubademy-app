package com.fiuba.ubademy.network

import com.fiuba.ubademy.network.model.AddCourseRequest
import retrofit2.Response
import retrofit2.http.*

interface UbademyCoursesApiService {
    @POST("courses")
    suspend fun addCourse(@Body addCourseRequest: AddCourseRequest)
            : Response<Void>
}