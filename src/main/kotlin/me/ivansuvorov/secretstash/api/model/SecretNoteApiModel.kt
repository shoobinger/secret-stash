package me.ivansuvorov.secretstash.api.model

import java.time.Instant
import java.util.UUID

data class SecretNoteApiModel(
    val id: UUID,
    val title: String,
    val content: String,
    val expiresAt: Instant?
)