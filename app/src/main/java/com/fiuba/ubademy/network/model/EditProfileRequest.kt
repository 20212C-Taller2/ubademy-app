package com.fiuba.ubademy.network.model

data class EditProfileRequest(
    val firstName: String,
    val lastName: String,
    val email: String
)