package com.fiuba.ubademy.network

import com.fiuba.ubademy.network.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://ubademy-users-api.herokuapp.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface UbademyApiService {
    @POST("register")
    suspend fun createAccount(@Body createAccountRequest: CreateAccountRequest)
        : Response<CreateAccountResponse>

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest)
        : Response<LoginResponse>

    @PATCH("users/{userId}")
    suspend fun editProfile(@Path("userId") userId : String, @Body editProfileRequest: EditProfileRequest, @Header("Authorization") authHeader : String)
        : Response<Void>

    object UbademyApi {
        val retrofitService : UbademyApiService by lazy {
            retrofit.create(UbademyApiService::class.java) }
    }
}