package com.ivansuvorov.secretstash.service.model

import java.time.Instant

data class SecretNoteCreateRequestDto(
    val title: String,
    val content: String,
    val expiresAt: Instant?,
)
