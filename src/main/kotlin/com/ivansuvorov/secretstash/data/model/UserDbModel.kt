package com.ivansuvorov.secretstash.data.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("users")
data class UserDbModel(
    @Id val id: UUID?,
    val email: String,
    val passwordHash: String,
)
