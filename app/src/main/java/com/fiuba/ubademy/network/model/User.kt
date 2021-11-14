package com.fiuba.ubademy.network.model

data class User(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val placeId: String?,
    val email: String,
    val interests: Set<String>
)