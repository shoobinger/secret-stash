package com.ivansuvorov.secretstash.service.model

import java.time.Instant

data class SecretNoteUpdateRequestDto(
    val title: String,
    val content: String,
    val expiresAt: Instant?,
)
