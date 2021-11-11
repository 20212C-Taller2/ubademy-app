package com.fiuba.ubademy.network.model

data class LoginResponse(
    val auth: Boolean,
    val token: String,
    val user: User,
    val googleData: GoogleData?
)