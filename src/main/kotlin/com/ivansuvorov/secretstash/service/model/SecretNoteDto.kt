package com.ivansuvorov.secretstash.service.model

import java.time.Instant
import java.util.UUID

data class SecretNoteDto(
    val id: UUID,
    val title: String,
    val content: String,
    val status: SecretNoteStatus,
    val expiresAt: Instant?,
    val createdAt: Instant,
)
