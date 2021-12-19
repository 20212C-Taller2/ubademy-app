package com.fiuba.ubademy.network

import com.fiuba.ubademy.network.model.*
import retrofit2.Response
import retrofit2.http.*

interface UbademyApiService {
    // region users
    @POST("users/register")
    suspend fun createAccount(@Body createAccountRequest: CreateAccountRequest)
            : Response<LoginResponse>

    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest)
            : Response<LoginResponse>

    @POST("users/login/google")
    suspend fun loginWithGoogle(@Body loginWithGoogleRequest: LoginWithGoogleRequest)
            : Response<LoginResponse>

    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String)
            : Response<GetUserResponse>

    @GET("users")
    suspend fun getUsers(@Query("appUsers") appUsers: Boolean = true, @Query("offset") offset: Int, @Query("limit") limit: Int)
            : Response<GetUsersResponse>

    @POST("users")
    suspend fun getUsersByIds(@Body userIds: List<String>)
            : Response<List<GetUserResponse>>

    @PATCH("users/{userId}")
    suspend fun editProfile(@Path("userId") userId: String, @Body editProfileRequest: EditProfileRequest)
            : Response<Void>

    @PATCH("users/{userId}")
    suspend fun updateFcmToken(@Path("userId") userId: String, @Body updateFcmTokenRequest: UpdateFcmTokenRequest)
            : Response<Void>

    @POST("users/notify")
    suspend fun notify(@Body notifyRequest: NotifyRequest)
            : Response<Void>
    // endregion

    // region courses
    @GET("courses/types")
    suspend fun getCourseTypes()
            : Response<List<String>>

    @GET("courses")
    suspend fun getCourses(@Query("creator") creatorUserId: String?, @Query("skip") skip: Int, @Query("limit") limit: Int)
            : Response<List<Course>>

    @GET("courses/students/{studentId}")
    suspend fun getStudentCourses(@Path("studentId") studentId: String, @Query("skip") skip: Int, @Query("limit") limit: Int)
            : Response<List<Course>>

    @GET("courses/collaborators/{collaboratorId}")
    suspend fun getCollaboratorCourses(@Path("collaboratorId") collaboratorId: String, @Query("skip") skip: Int, @Query("limit") limit: Int)
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

    @PATCH("courses/{courseId}")
    suspend fun editCourse(@Path("courseId") courseId: Int, @Body editCourseRequest: EditCourseRequest)
            : Response<Course>

    @POST("courses/{courseId}/students/{studentId}")
    suspend fun enrollStudent(@Path("courseId") courseId: Int, @Path("studentId") studentId: String)
            : Response<Void>

    @POST("courses/{courseId}/collaborators/{collaboratorId}")
    suspend fun enrollCollaborator(@Path("courseId") courseId: Int, @Path("collaboratorId") collaboratorId: String)
            : Response<Void>

    @DELETE("courses/{courseId}/students/{studentId}")
    suspend fun unenrollStudent(@Path("courseId") courseId: Int, @Path("studentId") studentId: String)
            : Response<Void>
    // endregion

    // region exams
    @GET("courses/{courseId}/exams")
    suspend fun getExams(@Path("courseId") courseId: Int, @Query("published") published: Boolean? = null)
            : Response<List<Exam>>

    @POST("courses/{courseId}/exams")
    suspend fun addExam(@Path("courseId") courseId: Int, @Body upsertExamRequest: UpsertExamRequest)
            : Response<Void>

    @PUT("courses/{courseId}/exams/{examId}")
    suspend fun editExam(@Path("courseId") courseId: Int, @Path("examId") examId: Int, @Body upsertExamRequest: UpsertExamRequest)
            : Response<Void>

    @GET("courses/{courseId}/exams/submissions")
    suspend fun getExamSubmissions(
        @Path("courseId") courseId: Int,
        @Query("student_id") studentId: String?,
        @Query("exam_id") examId: Int?,
        @Query("skip") skip: Int,
        @Query("limit") limit: Int)
            : Response<List<ExamSubmission>>

    @POST("courses/{courseId}/exams/{examId}")
    suspend fun submitExam(
        @Path("courseId") courseId: Int,
        @Path("examId") examId: Int,
        @Body submitExamRequest: SubmitExamRequest)
            : Response<Void>

    @PATCH("courses/{courseId}/exams/{submittedExamId}")
    suspend fun reviewExam(
        @Path("courseId") courseId: Int,
        @Path("submittedExamId") submittedExamId: Int,
        @Body examReview: ExamReview)
            : Response<Void>
    // endregion

    // region subscriptions
    @GET("subscriptions")
    suspend fun getSubscriptions()
            : Response<List<Subscription>>

    @GET("subscriptions/subscribers/{userId}")
    suspend fun getSubscriber(@Path("userId") userId: String)
            : Response<Subscriber>

    @POST("subscribers/{userId}/subscription")
    suspend fun subscribe(@Path("userId") userId: String, @Body subscribeRequest: SubscribeRequest)
            : Response<Void>
    // endregion
}