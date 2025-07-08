package me.ivansuvorov.secretstash.service

import me.ivansuvorov.secretstash.data.model.SecretNoteDbModel
import me.ivansuvorov.secretstash.data.repository.SecretNoteRepository
import me.ivansuvorov.secretstash.service.model.SecretNote
import me.ivansuvorov.secretstash.service.model.SecretNoteCreateRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SecretNoteService(
    private val secretNoteRepository: SecretNoteRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(SecretNoteService::class.java)

    fun create(request: SecretNoteCreateRequest): SecretNote {
        logger.info("Creating a new secret note")

        val secretNoteDbModel = SecretNoteDbModel(
            id = UUID.randomUUID(),
            title = request.title,
            content = request.content,
            expiresAt = request.expiresAt
        )

        secretNoteRepository.save(secretNoteDbModel)
        return secretNoteDbModel.toModel()
    }

    fun findById(id: UUID): SecretNote? {
        val secretNoteDbModel = secretNoteRepository.findByIdOrNull(id)
        val secretNote = secretNoteDbModel?.toModel()
        return secretNote
    }

    fun SecretNoteDbModel.toModel(): SecretNote {
        return SecretNote(
            id = id,
            title = title,
            content = content,
            expiresAt = expiresAt
        )
    }
}