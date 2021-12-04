package com.fiuba.ubademy.network

import com.fiuba.ubademy.network.model.*
import retrofit2.Response
import retrofit2.http.*

interface UbademyApiService {
    // region users
    @POST("users/register")
    suspend fun createAccount(@Body createAccountRequest: CreateAccountRequest)
            : Response<Void>

    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest)
            : Response<LoginResponse>

    @POST("users/login/google")
    suspend fun loginWithGoogle(@Body loginWithGoogleRequest: LoginWithGoogleRequest)
            : Response<LoginResponse>

    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String)
            : Response<GetUserResponse>

    @PATCH("users/{userId}")
    suspend fun editProfile(@Path("userId") userId: String, @Body editProfileRequest: EditProfileRequest)
            : Response<Void>
    // endregion

    // region courses
    @GET("courses/types")
    suspend fun getCourseTypes()
            : Response<List<String>>

    @GET("courses")
    suspend fun getCourses(@Query("creator") creatorUserId: String?, @Query("skip") skip: Int, @Query("limit") limit: Int)
            : Response<List<Course>>

    @GET("courses")
    suspend fun getCoursesFiltered(
        @Query("type") type: String?,
        @Query("subscription") subscription: String?,
        @Query("skip") skip: Int,
        @Query("limit") limit: Int)
            : Response<List<Course>>

    @POST("courses")
    suspend fun addCourse(@Body addCourseRequest: AddCourseRequest)
            : Response<Course>

    @POST("courses/{courseId}/students/{studentId}")
    suspend fun enrollStudent(@Path("courseId") courseId: Int, @Path("studentId") studentId: String)
            : Response<Void>

    @DELETE("courses/{courseId}/students/{studentId}")
    suspend fun unenrollStudent(@Path("courseId") courseId: Int, @Path("studentId") studentId: String)
            : Response<Void>
    // endregion

    // region subscriptions
    @GET("subscriptions")
    suspend fun getSubscriptions()
            : Response<List<Subscription>>

    @GET("subscriptions/users/{userId}")
    suspend fun getUserFinancialInformation(@Path("userId") userId: String)
            : Response<UserFinancialInformation>

    @POST("subscriptions")
    suspend fun subscribe(@Body subscribeRequest: SubscribeRequest)
            : Response<Void>
    // endregion
}