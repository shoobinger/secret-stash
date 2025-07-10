package com.ivansuvorov.secretstash.service.model

import java.util.UUID

data class UserDto(
    val id: UUID,
    val email: String,
)
