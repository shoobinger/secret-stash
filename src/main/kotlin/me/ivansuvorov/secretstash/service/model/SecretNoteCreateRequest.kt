package me.ivansuvorov.secretstash.service.model

import java.time.Instant
import java.util.UUID

data class SecretNoteCreateRequest(
    val title: String,
    val content: String,
    val expiresAt: Instant?
)