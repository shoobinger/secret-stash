package com.ivansuvorov.secretstash.data.repository

import com.ivansuvorov.secretstash.data.model.SecretNoteDbModel
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface SecretNoteRepository : CrudRepository<SecretNoteDbModel, UUID> {
    fun findByIdAndOwnerIdAndStatus(id: UUID, ownerId: UUID, status: String): SecretNoteDbModel?
}