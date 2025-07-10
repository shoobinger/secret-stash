package com.ivansuvorov.secretstash.service

import com.ivansuvorov.secretstash.data.model.SecretNoteDbModel
import com.ivansuvorov.secretstash.data.repository.SecretNoteRepository
import com.ivansuvorov.secretstash.service.model.SecretNoteCreateRequestDto
import com.ivansuvorov.secretstash.service.model.SecretNoteDto
import com.ivansuvorov.secretstash.service.model.SecretNoteStatus
import com.ivansuvorov.secretstash.service.model.SecretNoteUpdateRequestDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.UUID

@Service
class SecretNoteService(
    private val secretNoteRepository: SecretNoteRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(SecretNoteService::class.java)

    @Transactional
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
        return secretNoteDbModel.toDto()
    }

    @Transactional
    fun update(id: UUID, request: SecretNoteUpdateRequestDto): SecretNoteDto {
        logger.info("Updating secret note with id $id")

        val secretNote = secretNoteRepository.findByIdAndStatus(id, SecretNoteStatus.ACTIVE.name)
        if (secretNote == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }

        val secretNoteDbModel = secretNoteRepository.save(
            SecretNoteDbModel(
                id = secretNote.id,
                title = request.title,
                content = request.content,
                status = secretNote.status,
                expiresAt = request.expiresAt,
                ownerId = secretNote.ownerId,
                createdAt = secretNote.createdAt
            )
        )
        return secretNoteDbModel.toDto()
    }

    @Transactional
    fun delete(id: UUID) {

        val secretNote = secretNoteRepository.findByIdAndStatus(id, SecretNoteStatus.ACTIVE.name)
        if (secretNote == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }

        secretNoteRepository.save(
            SecretNoteDbModel(
                id = secretNote.id,
                title = secretNote.title,
                content = secretNote.content,
                status = SecretNoteStatus.DELETED.name,
                expiresAt = secretNote.expiresAt,
                ownerId = secretNote.ownerId,
                createdAt = secretNote.createdAt
            )
        )
    }

    fun findById(id: UUID): SecretNoteDto? {
        val secretNote = secretNoteRepository.findByIdAndStatus(id, SecretNoteStatus.ACTIVE.name)
        if (secretNote == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        return secretNote.toDto()
    }

    fun SecretNoteDbModel.toDto(): SecretNoteDto {
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