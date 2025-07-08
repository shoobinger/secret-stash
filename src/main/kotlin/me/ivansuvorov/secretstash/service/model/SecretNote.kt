package me.ivansuvorov.secretstash.service.model

import java.time.Instant
import java.util.UUID

data class SecretNote(
    val id: UUID,
    val title: String,
    val content: String,
    val expiresAt: Instant?
)