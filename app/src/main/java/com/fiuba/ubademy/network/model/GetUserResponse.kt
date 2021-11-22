package com.fiuba.ubademy.network.model

data class GetUserResponse(
    val user: User,
    val googleData: GoogleData?
)
