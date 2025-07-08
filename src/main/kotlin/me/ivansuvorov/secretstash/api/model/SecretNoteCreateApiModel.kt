package me.ivansuvorov.secretstash.api.model

import java.time.Instant

data class SecretNoteCreateApiModel(
    val title: String,
    val content: String,
    val expiresAt: Instant?
)