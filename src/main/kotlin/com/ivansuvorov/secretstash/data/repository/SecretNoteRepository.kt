package com.ivansuvorov.secretstash.data.repository

import com.ivansuvorov.secretstash.data.model.SecretNoteDbModel
import com.ivansuvorov.secretstash.data.model.SecretNoteType
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface SecretNoteRepository : CrudRepository<SecretNoteDbModel, UUID> {
    fun findByIdAndOwnerIdAndStatus(
        id: UUID,
        ownerId: UUID,
        status: String,
    ): SecretNoteDbModel?

    fun findByOwnerIdAndStatusOrderByCreatedAtDesc(
        ownerId: UUID,
        status: String,
        pageable: Pageable,
    ): List<SecretNoteDbModel>

    fun findByIdAndType(id: UUID, type: SecretNoteType): SecretNoteDbModel?

    @Modifying
    @Query("UPDATE secret_note SET status = 'EXPIRED' WHERE expires_at < CURRENT_TIMESTAMP")
    fun updateExpiredStatus()
}
