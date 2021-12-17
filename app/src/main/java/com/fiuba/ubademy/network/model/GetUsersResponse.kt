package com.fiuba.ubademy.network.model

data class GetUsersResponse(
    val users: List<GetUserResponse>,
    val total: Int
)