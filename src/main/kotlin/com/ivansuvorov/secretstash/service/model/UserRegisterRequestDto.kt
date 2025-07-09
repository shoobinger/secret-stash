package com.ivansuvorov.secretstash.service.model

data class UserRegisterRequestDto(
    val email: String,
    val password: String
)