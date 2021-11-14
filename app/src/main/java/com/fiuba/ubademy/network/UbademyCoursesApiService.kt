package com.fiuba.ubademy.network

import com.fiuba.ubademy.network.model.AddCourseRequest
import com.fiuba.ubademy.network.model.Course
import retrofit2.Response
import retrofit2.http.*

interface UbademyCoursesApiService {
    @GET("courses/types")
    suspend fun getCourseTypes()
        : Response<List<String>>

    @GET("courses")
    suspend fun getCourses(@Query("skip") skip: Int, @Query("limit") limit: Int)
        : Response<List<Course>>

    @POST("courses")
    suspend fun addCourse(@Body addCourseRequest: AddCourseRequest)
        : Response<Void>
}