package com.ivansuvorov.secretstash.service

import com.ivansuvorov.secretstash.data.model.SecretNoteDbModel
import com.ivansuvorov.secretstash.data.repository.SecretNoteRepository
import com.ivansuvorov.secretstash.service.model.SecretNoteCreateRequestDto
import com.ivansuvorov.secretstash.service.model.SecretNoteDto
import com.ivansuvorov.secretstash.service.model.SecretNoteStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class SecretNoteService(
    private val secretNoteRepository: SecretNoteRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(SecretNoteService::class.java)

    fun create(request: SecretNoteCreateRequestDto): SecretNoteDto {
        logger.info("Creating a new secret note")

        val secretNoteDbModel = secretNoteRepository.save(
            SecretNoteDbModel(
                id = null,
                title = request.title,
                content = request.content,
                status = SecretNoteStatus.ACTIVE.name,
                expiresAt = request.expiresAt,
                ownerId = UUID.randomUUID(), // TODO
                createdAt = Instant.now()
            )
        )
        return secretNoteDbModel.toModel()
    }

    fun findById(id: UUID): SecretNoteDto? {
        val secretNoteDbModel = secretNoteRepository.findByIdOrNull(id)
        val secretNote = secretNoteDbModel?.toModel()
        return secretNote
    }

    fun SecretNoteDbModel.toModel(): SecretNoteDto {
        return SecretNoteDto(
            id = checkNotNull(id),
            title = title,
            content = content,
            status = SecretNoteStatus.valueOf(status),
            expiresAt = expiresAt,
            createdAt = createdAt
        )
    }
}