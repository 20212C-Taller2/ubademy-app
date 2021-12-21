package com.fiuba.ubademy.network.model

data class NotifyRequest(
    val from: String,
    val to: String,
    val message: String
)