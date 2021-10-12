package com.fiuba.ubademy.network.model

data class CreateAccountResponse(
    val auth: Boolean,
    val token: String,
    val user: User
)