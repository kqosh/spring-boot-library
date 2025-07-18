package com.jdriven.library.service.model

data class CreateJwtRequest(
    val username: String,
    val password: String,
)