package com.fiuba.ubademy.network

import com.fiuba.ubademy.network.model.*
import retrofit2.Response
import retrofit2.http.*

interface UbademyApiService {
    @POST("users/register")
    suspend fun createAccount(@Body createAccountRequest: CreateAccountRequest)
        : Response<Void>

    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest)
        : Response<LoginResponse>

    @POST("users/login/google")
    suspend fun loginWithGoogle(@Body loginWithGoogleRequest: LoginWithGoogleRequest)
        : Response<LoginResponse>

    @PATCH("users/{userId}")
    suspend fun editProfile(@Path("userId") userId : String, @Body editProfileRequest: EditProfileRequest)
        : Response<Void>

    @GET("courses/types")
    suspend fun getCourseTypes()
        : Response<List<String>>

    @GET("courses")
    suspend fun getCourses(@Query("skip") skip: Int, @Query("limit") limit: Int)
        : Response<List<Course>>

    @GET("courses")
    suspend fun getCoursesFiltered(@Query("type") type: String, @Query("subscription") subscription: String, @Query("skip") skip: Int, @Query("limit") limit: Int)
        : Response<List<Course>>

    @POST("courses")
    suspend fun addCourse(@Body addCourseRequest: AddCourseRequest)
        : Response<Void>

    @GET("subscriptions")
    suspend fun getSubscriptions()
        : Response<List<Subscription>>
}