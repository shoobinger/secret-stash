package me.ivansuvorov.secretstash.data.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("secret_note")
data class SecretNoteDbModel(
    @Id val id: UUID,
    val title: String,
    val content: String,
    val expiresAt: Instant?
)