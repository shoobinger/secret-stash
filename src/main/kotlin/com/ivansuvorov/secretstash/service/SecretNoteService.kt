package com.ivansuvorov.secretstash.service

import com.ivansuvorov.secretstash.data.model.SecretNoteDbModel
import com.ivansuvorov.secretstash.data.repository.SecretNoteRepository
import com.ivansuvorov.secretstash.service.model.SecretNoteCreateRequestDto
import com.ivansuvorov.secretstash.service.model.SecretNoteDto
import com.ivansuvorov.secretstash.service.model.SecretNoteStatus
import com.ivansuvorov.secretstash.service.model.SecretNoteUpdateRequestDto
import com.ivansuvorov.secretstash.service.model.UserDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.UUID

@Service
class SecretNoteService(
    private val secretNoteRepository: SecretNoteRepository,
) {
    private val logger: Logger = LoggerFactory.getLogger(SecretNoteService::class.java)

    companion object {
        const val MAX_TITLE_LENGTH = 100
        const val MAX_CONTENT_LENGTH = 5000
    }

    @Transactional
    fun create(
        caller: UserDto,
        request: SecretNoteCreateRequestDto,
    ): SecretNoteDto {
        logger.info("Creating a new secret note")

        validateTitle(request.title)
        validateContent(request.content)
        validateExpirationDate(request.expiresAt)

        val secretNoteDbModel =
            secretNoteRepository.save(
                SecretNoteDbModel(
                    id = null,
                    title = request.title,
                    content = request.content,
                    status = SecretNoteStatus.ACTIVE.name,
                    expiresAt = request.expiresAt,
                    ownerId = caller.id,
                    createdAt = Instant.now(),
                ),
            )

        return secretNoteDbModel.toDto()
    }

    @Transactional
    fun update(
        caller: UserDto,
        noteId: UUID,
        request: SecretNoteUpdateRequestDto,
    ): SecretNoteDto {
        logger.info("Updating secret note")

        val secretNote =
            secretNoteRepository.findByIdAndOwnerIdAndStatus(
                id = noteId,
                ownerId = caller.id,
                status = SecretNoteStatus.ACTIVE.name,
            ) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        validateTitle(request.title)
        validateContent(request.content)
        validateExpirationDate(request.expiresAt)

        val secretNoteDbModel =
            secretNoteRepository.save(
                SecretNoteDbModel(
                    id = secretNote.id,
                    title = request.title,
                    content = request.content,
                    status = secretNote.status,
                    expiresAt = request.expiresAt,
                    ownerId = secretNote.ownerId,
                    createdAt = secretNote.createdAt,
                ),
            )
        return secretNoteDbModel.toDto()
    }

    @Transactional
    fun delete(
        caller: UserDto,
        noteId: UUID,
    ) {
        logger.info("Deleting secret note")

        val secretNote =
            secretNoteRepository.findByIdAndOwnerIdAndStatus(
                id = noteId,
                ownerId = caller.id,
                status = SecretNoteStatus.ACTIVE.name,
            ) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Secret note not found or inaccessible")

        secretNoteRepository.save(
            SecretNoteDbModel(
                id = secretNote.id,
                title = secretNote.title,
                content = secretNote.content,
                status = SecretNoteStatus.DELETED.name,
                expiresAt = secretNote.expiresAt,
                ownerId = secretNote.ownerId,
                createdAt = secretNote.createdAt,
            ),
        )
    }

    @Transactional
    fun findById(
        caller: UserDto,
        noteId: UUID,
    ): SecretNoteDto? {
        logger.debug("Getting secret note by ID")

        val secretNote =
            secretNoteRepository.findByIdAndOwnerIdAndStatus(
                id = noteId,
                ownerId = caller.id,
                status = SecretNoteStatus.ACTIVE.name,
            ) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Secret note not found or inaccessible")

        return secretNote.toDto()
    }

    @Transactional
    fun findLatest(
        caller: UserDto,
        count: Int,
    ): List<SecretNoteDto> {
        logger.debug("Finding latest secret notes, count {}", count)

        val pageRequest = PageRequest.of(0, count)
        return secretNoteRepository
            .findByOwnerIdAndStatusOrderByCreatedAtDesc(
                ownerId = caller.id,
                status = SecretNoteStatus.ACTIVE.name,
                pageable = pageRequest,
            ).map { it.toDto() }
    }

    @Scheduled(fixedRate = 1000)
    @Transactional
    fun handleExpiredNotes() {
        logger.trace("Performing scheduled job to update expired notes statuses")
        secretNoteRepository.updateExpiredStatus()
    }

    private fun validateTitle(title: String) {
        require(title.length <= MAX_TITLE_LENGTH) {
            "The length of the title must be less than $MAX_TITLE_LENGTH"
        }
    }

    private fun validateContent(content: String) {
        require(content.length <= MAX_CONTENT_LENGTH) {
            "The length of the content must be less than $MAX_CONTENT_LENGTH"
        }
    }

    private fun validateExpirationDate(expiresAt: Instant?) {
        require(expiresAt == null || expiresAt.isAfter(Instant.now())) {
            "Expiration date must be in the future"
        }
    }

    private fun SecretNoteDbModel.toDto(): SecretNoteDto = SecretNoteDto(
        id = checkNotNull(id),
        title = title,
        content = content,
        status = SecretNoteStatus.valueOf(status),
        expiresAt = expiresAt,
        createdAt = createdAt,
    )
}
