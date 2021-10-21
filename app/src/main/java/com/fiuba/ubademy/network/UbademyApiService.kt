package com.fiuba.ubademy.network

import com.fiuba.ubademy.network.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface UbademyApiService {
    @POST("register")
    suspend fun createAccount(@Body createAccountRequest: CreateAccountRequest)
        : Response<CreateAccountResponse>

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest)
        : Response<LoginResponse>

    @PATCH("users/{userId}")
    suspend fun editProfile(@Path("userId") userId : String, @Body editProfileRequest: EditProfileRequest)
        : Response<Void>
}